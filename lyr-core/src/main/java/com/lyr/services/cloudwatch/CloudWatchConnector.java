package com.lyr.services.cloudwatch;

import com.lyr.services.ServiceProvider;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

@RequiredArgsConstructor
public final class CloudWatchConnector {
    private static final String AWS_DYNAMO_DB = "AWS/DynamoDB";
    private static final String CONSUMED_READ_CAPACITY_UNITS = "ConsumedReadCapacityUnits";
    private static final String CONSUMED_WRITE_CAPACITY_UNITS = "ConsumedWriteCapacityUnits";
    private static final String TABLE_NAME = "TableName";

    private final CloudWatchClient client;
    private final CloudWatchLogsClient logsClient;

    public static CloudWatchConnector create() {
        return new CloudWatchConnector(
                ServiceProvider.getOrBuildCloudWatchClient(), ServiceProvider.getOrBuildCloudWatchLogsClient());
    }

    static CloudWatchConnector create(final CloudWatchClient cloudWatchClient, final CloudWatchLogsClient logsClient) {
        return new CloudWatchConnector(cloudWatchClient, logsClient);
    }

    public List<LogGroup> listLogGroupDetails() {
        return logsClient.describeLogGroupsPaginator().stream()
                .map(DescribeLogGroupsResponse::logGroups)
                .flatMap(List::stream)
                .toList();
    }

    public double getTotalConsumedReadCapacityOfADynamoDbTable(
            final String tableName, final Instant startTime, final Instant endTime, final int periodInSeconds) {
        return getSummedDynamoDbTableMetric(
                tableName, CONSUMED_READ_CAPACITY_UNITS, startTime, endTime, periodInSeconds);
    }

    public double getTotalConsumedWriteCapacityOfADynamoDbTable(
            final String tableName, final Instant startTime, final Instant endTime, final int periodInSeconds) {
        return getSummedDynamoDbTableMetric(
                tableName, CONSUMED_WRITE_CAPACITY_UNITS, startTime, endTime, periodInSeconds);
    }

    private double getSummedDynamoDbTableMetric(
            final String tableName,
            final String metricName,
            final Instant startTime,
            final Instant endTime,
            final int periodInSeconds) {
        final var statistics = client.getMetricStatistics(GetMetricStatisticsRequest.builder()
                .metricName(metricName)
                .startTime(startTime)
                .endTime(endTime)
                .period(periodInSeconds)
                .namespace(AWS_DYNAMO_DB)
                .statistics(Statistic.SUM)
                .dimensions(
                        Dimension.builder().name(TABLE_NAME).value(tableName).build())
                .build());

        return statistics.datapoints().stream()
                .map(Datapoint::sum)
                .reduce(Double::sum)
                .orElse(0d);
    }
}
