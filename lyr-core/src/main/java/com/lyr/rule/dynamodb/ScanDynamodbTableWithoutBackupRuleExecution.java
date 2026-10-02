package com.lyr.rule.dynamodb;

import static com.lyr.services.util.ArnUtil.WILDCARD_SYMBOL;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutBackupRuleConfig;
import com.lyr.services.backup.BackupConnector;
import com.lyr.services.dynamodb.DynamoDbConnector;
import com.lyr.services.tag.TaggingConnector;
import com.lyr.services.util.ArnUtil;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.backup.model.BackupSelection;
import software.amazon.awssdk.services.dynamodb.model.ContinuousBackupsDescription;
import software.amazon.awssdk.services.dynamodb.model.PointInTimeRecoveryStatus;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;

@Slf4j
public class ScanDynamodbTableWithoutBackupRuleExecution
        implements RuleExecutionStrategy<ScanDynamodbTableWithoutBackupRuleConfig> {
    private static final String AWS_RESOURCE_TAG_PREFIX = "aws:ResourceTag/";

    private DynamoDbConnector dynamoDbConnector;
    private BackupConnector backupConnector;
    private TaggingConnector taggingConnector;

    @Override
    public ImmutableList<Finding> execute(final ScanDynamodbTableWithoutBackupRuleConfig config) {
        dynamoDbConnector = DynamoDbConnector.create();
        backupConnector = BackupConnector.create();
        taggingConnector = TaggingConnector.create();

        final var allAvailableTables = dynamoDbConnector.listTableNames();
        var tablesInQuestion = Set.copyOf(allAvailableTables);

        if (config.getPassIfPitrEnabled() && !tablesInQuestion.isEmpty()) {
            tablesInQuestion = filterTablesWithoutPitr(tablesInQuestion);
        }

        if (config.getPassIfBackupPlanEnabled() && !tablesInQuestion.isEmpty()) {
            tablesInQuestion = filterTablesWithoutBackupPlan(tablesInQuestion);
        }

        final var findings = tablesInQuestion.stream().map(Finding::byId).collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(allAvailableTables.size())
                .log("Found {} table(s) with problems out of {} table(s).");

        return findings;
    }

    private Set<String> filterTablesWithoutPitr(final Set<String> tableNames) {
        return tableNames.stream()
                .filter(name -> dynamoDbConnector
                        .getContinuousBackupsDescription(name)
                        .filter(this::isPitrDisabled)
                        .isPresent())
                .collect(Collectors.toSet());
    }

    private boolean isPitrDisabled(final ContinuousBackupsDescription continuousBackupsDescription) {
        final var pitrDescriptions = continuousBackupsDescription.pointInTimeRecoveryDescription();

        return null == pitrDescriptions
                || PointInTimeRecoveryStatus.DISABLED.equals(pitrDescriptions.pointInTimeRecoveryStatus());
    }

    private Set<String> filterTablesWithoutBackupPlan(final Set<String> tableNames) {
        final var tableDescriptions = tableNames.stream()
                .map(dynamoDbConnector::getTableDescription)
                .flatMap(Optional::stream)
                .toList();

        final var tableArns =
                tableDescriptions.stream().map(TableDescription::tableArn).toArray(String[]::new);

        final var tableArnToTagsMap = taggingConnector.getResourceTagMappingForResources(tableArns);
        final var backupSelections = backupConnector.listAllBackupPlanSelections();

        return tableDescriptions.stream()
                .filter(tableDesc -> !isCoveredByBackupPlan(
                        tableDesc.tableArn(), tableArnToTagsMap.get(tableDesc.tableArn()), backupSelections))
                .map(TableDescription::tableName)
                .collect(Collectors.toSet());
    }

    private boolean isCoveredByBackupPlan(
            final String tableArn, final List<Tag> tableTags, final List<BackupSelection> backupSelections) {
        for (final BackupSelection backupSelection : backupSelections) {
            if (!backupSelection.hasResources() && isSupportedConditionsNotProvided(backupSelection)) {
                // Out of scope of checks and due to that we can't guarantee coverage and fail by default
                continue;
            }

            final var optAnyResourcePatternMatches = anyResourcePatternMatches(tableArn, backupSelection);

            // If there are no patterns, we can't fail yet as it might be covered only by tags
            if (optAnyResourcePatternMatches.isPresent() && !optAnyResourcePatternMatches.get()) {
                continue;
            }

            final var optAreAllTagConditionsMatches = allTagConditionsSatisfied(tableTags, backupSelection);

            // If there are no tags, we can't fail yet as it might be covered only by patterns
            if (optAreAllTagConditionsMatches.isEmpty() || optAreAllTagConditionsMatches.get()) {
                return true;
            }
        }

        return false;
    }

    private Optional<Boolean> anyResourcePatternMatches(final String tableArn, final BackupSelection backupSelection) {
        if (!backupSelection.hasResources()) {
            return Optional.empty();
        }

        return Optional.of(backupSelection.resources().stream()
                .anyMatch(pattern -> WILDCARD_SYMBOL.equals(pattern) || ArnUtil.isMatching(pattern, tableArn)));
    }

    private Optional<Boolean> allTagConditionsSatisfied(
            final List<Tag> tableTags, final BackupSelection backupSelection) {
        if (isSupportedConditionsNotProvided(backupSelection)) {
            return Optional.empty();
        }

        return Optional.of(backupSelection.conditions().stringEquals().stream()
                .allMatch(condition -> tableTags.stream()
                        .anyMatch(tag -> tag.key().equals(trimTagPrefixFromConditionKey(condition.conditionKey()))
                                && tag.value().equals(condition.conditionValue()))));
    }

    private boolean isSupportedConditionsNotProvided(final BackupSelection backupSelection) {
        return backupSelection.conditions() == null
                || !backupSelection.conditions().hasStringEquals();
    }

    private String trimTagPrefixFromConditionKey(final String conditionKey) {
        return conditionKey.replace(AWS_RESOURCE_TAG_PREFIX, "");
    }
}
