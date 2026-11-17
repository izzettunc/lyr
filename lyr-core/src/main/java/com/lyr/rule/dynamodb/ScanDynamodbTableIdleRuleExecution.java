package com.lyr.rule.dynamodb;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import com.lyr.services.cloudwatch.CloudWatchUtil;
import com.lyr.services.dynamodb.DynamoDbConnector;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

@Slf4j
public class ScanDynamodbTableIdleRuleExecution implements RuleExecutionStrategy<ScanDynamodbTableIdleRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanDynamodbTableIdleRuleConfig parameters) {
        final var dynamoDbConnector = DynamoDbConnector.create();
        final var cloudWatchConnector = CloudWatchConnector.create();

        final var timeRange = calculateTimeRange(parameters);

        final var listOfTableNames = dynamoDbConnector.listTableNames();

        final var findings = listOfTableNames.stream()
                .filter(tableName -> {
                    final var optTable = dynamoDbConnector.getTableDescription(tableName);
                    return optTable.isPresent()
                            && isCreatedBefore(optTable.get(), timeRange.startTime())
                            && (!parameters.getExcludeEmptyTables()
                                    || optTable.get().tableSizeBytes() != 0);
                })
                .filter(tableName -> {
                    final var totalConsumedReadCapacity =
                            cloudWatchConnector.getTotalConsumedReadCapacityOfADynamoDbTable(tableName, timeRange);
                    final var totalConsumedWriteCapacity =
                            cloudWatchConnector.getTotalConsumedWriteCapacityOfADynamoDbTable(tableName, timeRange);

                    return totalConsumedReadCapacity == 0 && totalConsumedWriteCapacity == 0;
                })
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(listOfTableNames.size())
                .log("Found {} table(s) with problems out of {} table(s).");

        return findings;
    }

    // Tables younger than the idle period can't have a full window of CloudWatch data, so they're never idle
    private static boolean isCreatedBefore(final TableDescription table, final Instant threshold) {
        return table.creationDateTime() == null || table.creationDateTime().isBefore(threshold);
    }

    private static CloudWatchConnector.TimeRange calculateTimeRange(final ScanDynamodbTableIdleRuleConfig config) {

        final var now = Instant.now();
        final var then = now.minus(Period.ofDays(config.getMaxIdlePeriodInDays()));
        final var appropriateTimeWindow =
                CloudWatchUtil.getAppropriateTimeWindowForPeriod(config.getMaxIdlePeriodInDays());
        final var periodInSeconds = (int) Duration.ofDays(appropriateTimeWindow).getSeconds();

        return CloudWatchConnector.TimeRange.builder()
                .startTime(then)
                .endTime(now)
                .periodInSeconds(periodInSeconds)
                .build();
    }
}
