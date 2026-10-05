package com.lyr.rule.dynamodb;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutDeletionProtectionRuleConfig;
import com.lyr.services.dynamodb.DynamoDbConnector;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

@Slf4j
public class ScanDynamodbTableWithoutDeletionProtectionRuleExecution
        implements RuleExecutionStrategy<ScanDynamodbTableWithoutDeletionProtectionRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanDynamodbTableWithoutDeletionProtectionRuleConfig parameters) {
        final var dynamodbConnector = DynamoDbConnector.create();

        final var tableNames = dynamodbConnector.listTableNames();

        final var findings = tableNames.stream()
                .map(dynamodbConnector::getTableDescription)
                .flatMap(Optional::stream)
                .filter(Predicate.not(TableDescription::deletionProtectionEnabled))
                .map(TableDescription::tableName)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(tableNames.size())
                .log("Found {} table(s) with problems out of {} table(s).");

        return findings;
    }
}
