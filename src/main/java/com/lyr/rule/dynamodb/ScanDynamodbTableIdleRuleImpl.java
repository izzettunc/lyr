package com.lyr.rule.dynamodb;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.RuleStrategy;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import com.lyr.services.cloudwatch.CloudWatchUtil;
import com.lyr.services.dynamodb.DynamoDbConnector;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public class ScanDynamodbTableIdleRuleImpl implements RuleStrategy<ScanDynamodbTableIdleRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(final ScanDynamodbTableIdleRuleConfig parameters) {
        final var dynamoDbConnector = DynamoDbConnector.create();
        final var cloudWatchConnector = CloudWatchConnector.create();

        final var now = Instant.now();
        final var then = now.minus(Period.ofDays(parameters.getMaxIdlePeriodInDays()));

        final var appropriateTimeWindow =
                CloudWatchUtil.getAppropriateTimeWindowForPeriod(parameters.getMaxIdlePeriodInDays());
        final var periodInSeconds = (int) Duration.ofDays(appropriateTimeWindow).getSeconds();

        return dynamoDbConnector.listTableNames().stream()
                .filter(tableName -> {
                    final var totalConsumedReadCapacity =
                            cloudWatchConnector.getTotalConsumedReadCapacityOfADynamoDbTable(
                                    tableName, then, now, periodInSeconds);
                    final var totalConsumedWriteCapacity =
                            cloudWatchConnector.getTotalConsumedWriteCapacityOfADynamoDbTable(
                                    tableName, then, now, periodInSeconds);

                    return totalConsumedReadCapacity == 0 && totalConsumedWriteCapacity == 0;
                })
                .filter(tableName -> {
                    final var optTable = dynamoDbConnector.getTable(tableName);
                    return optTable.isPresent()
                            && (!parameters.getExcludeEmptyTables()
                                    || optTable.get().table().tableSizeBytes() != 0);
                })
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());
    }
}
