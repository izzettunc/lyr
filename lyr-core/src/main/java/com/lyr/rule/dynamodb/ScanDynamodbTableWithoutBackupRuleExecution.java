package com.lyr.rule.dynamodb;

import static java.util.Arrays.stream;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutBackupRuleConfig;
import com.lyr.services.backup.BackupConnector;
import com.lyr.services.dynamodb.DynamoDbConnector;
import com.lyr.services.tag.TaggingConnector;
import com.lyr.services.util.ArnUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.backup.model.BackupSelection;
import software.amazon.awssdk.services.dynamodb.model.ContinuousBackupsStatus;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;

public class ScanDynamodbTableWithoutBackupRuleExecution
        implements RuleExecutionStrategy<ScanDynamodbTableWithoutBackupRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanDynamodbTableWithoutBackupRuleConfig config) {
        final var dynamodbConnector = DynamoDbConnector.create();

        final var allAvailableTables = dynamodbConnector.listTableNames();
        var tablesInQuestion = Set.copyOf(allAvailableTables);

        if (config.getPassIfPitrEnabled()) {
            tablesInQuestion = tablesInQuestion.stream()
                    .filter(name -> dynamodbConnector
                            .getContinuousBackupsDescription(name)
                            .filter(continuousBackupsDescription -> ContinuousBackupsStatus.DISABLED.equals(
                                    continuousBackupsDescription.continuousBackupsStatus()))
                            .isPresent())
                    .collect(Collectors.toSet());
        }

        if (config.getPassIfBackupPlanEnabled()) {
            final var backupConnector = BackupConnector.create();
            final var taggingConnector = TaggingConnector.create();

            final var descriptionsOfTablesInQuestion = tablesInQuestion.stream()
                    .map(dynamodbConnector::getTableDescription)
                    .flatMap(Optional::stream)
                    .toList();

            final var tableArnToTagsMap = buildTableArnToTagsMap(taggingConnector, descriptionsOfTablesInQuestion);
            final var backupSelections = backupConnector.listAllBackupPlanSelections();

            tablesInQuestion = descriptionsOfTablesInQuestion.stream()
                    .filter(tableDesc -> !isCoveredByBackupPlan(
                            tableDesc.tableArn(), tableArnToTagsMap.get(tableDesc.tableArn()), backupSelections))
                    .map(TableDescription::tableName)
                    .collect(Collectors.toSet());
        }

        return tablesInQuestion.stream().map(Finding::byId).collect(ImmutableList.toImmutableList());
    }

    private boolean isCoveredByBackupPlan(
            final String tableArn, final List<Tag> tableTags, final List<BackupSelection> backupSelections) {
        final var isTableCoveredByPattern = backupSelections.stream()
                .flatMap(selection -> selection.resources().stream())
                .anyMatch(pattern -> ArnUtil.isMatching(tableArn, pattern));

        if (isTableCoveredByPattern) {
            return true;
        }

        return backupSelections.stream()
                .flatMap(selection -> selection.listOfTags().stream())
                .anyMatch(condition -> tableTags.stream()
                        .anyMatch(tag -> tag.key().equals(condition.conditionKey())
                                && tag.value().equals(condition.conditionValue())));
    }

    private Map<String, List<Tag>> buildTableArnToTagsMap(
            final TaggingConnector taggingConnector, final List<TableDescription> tableDescriptions) {
        final var allValidTableArns =
                tableDescriptions.stream().map(TableDescription::tableArn).toArray(String[]::new);
        final var tableArnToTagMap = taggingConnector.getResourceTagMappingForResources(allValidTableArns).stream()
                .collect(Collectors.toMap(ResourceTagMapping::resourceARN, ResourceTagMapping::tags));

        return stream(allValidTableArns)
                .collect(Collectors.toMap(
                        tableArn -> tableArn, tableArn -> tableArnToTagMap.getOrDefault(tableArn, new ArrayList<>())));
    }
}
