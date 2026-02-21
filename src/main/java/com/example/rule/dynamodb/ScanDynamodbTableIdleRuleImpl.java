package com.example.rule.dynamodb;

import com.example.rule.RuleStrategy;
import com.example.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.example.services.cloudwatch.CloudWatchConnector;
import com.example.services.cloudwatch.CloudWatchUtil;
import com.example.services.dynamodb.DynamoDbConnector;
import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.List;

public class ScanDynamodbTableIdleRuleImpl implements RuleStrategy<ScanDynamodbTableIdleRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(ScanDynamodbTableIdleRuleConfig parameters) {
        if (parameters.getMaxIdlePeriodInDays() < 1) {
            throw new IllegalArgumentException("Period must be longer than a day");
        }

        var dynamoDbConnector = DynamoDbConnector.create();
        var cloudWatchConnector = CloudWatchConnector.create();

        var now = Instant.now();
        var then = now.minus(Period.ofDays(parameters.getMaxIdlePeriodInDays()));

        var appropriateTimeWindow = CloudWatchUtil.getAppropriateTimeWindowForPeriod(parameters.getMaxIdlePeriodInDays());
        var periodInSeconds = (int) Duration.ofDays(appropriateTimeWindow).getSeconds();

        return dynamoDbConnector
                .listTables()
                .stream()
                .map(ListTablesResponse::tableNames)
                .flatMap(List::stream)
                .filter(tableName -> {
                    var totalConsumedReadCapacity = cloudWatchConnector
                            .getTotalConsumedReadCapacityOfADynamoDbTable(
                                    tableName, then, now, periodInSeconds);
                    var totalConsumedWriteCapacity = cloudWatchConnector
                            .getTotalConsumedWriteCapacityOfADynamoDbTable(
                                    tableName, then, now, periodInSeconds);

                    return totalConsumedReadCapacity == 0 && totalConsumedWriteCapacity == 0;
                })
                .filter(tableName -> {
                    var optTable = dynamoDbConnector.getTable(tableName);
                    return optTable.isPresent() && (!parameters.getExcludeEmptyTables() || optTable.get().table().tableSizeBytes() != 0);
                })
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());
    }
}
