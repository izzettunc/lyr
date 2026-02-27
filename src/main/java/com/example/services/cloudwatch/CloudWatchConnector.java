package com.example.services.cloudwatch;

import com.example.services.ServiceProvider;
import java.time.Instant;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;

public final class CloudWatchConnector {
    private static final String AWS_DYNAMO_DB = "AWS/DynamoDB";
    private static final String CONSUMED_READ_CAPACITY_UNITS = "ConsumedReadCapacityUnits";
    private static final String CONSUMED_WRITE_CAPACITY_UNITS = "ConsumedWriteCapacityUnits";
    private static final String TABLE_NAME = "TableName";

    private final CloudWatchClient client;

    private CloudWatchConnector(final CloudWatchClient cloudWatchClient) {
        this.client = cloudWatchClient;
    }

    public static CloudWatchConnector create() {
        return new CloudWatchConnector(ServiceProvider.getOrBuildCloudWatchClient());
    }

    static CloudWatchConnector create(final CloudWatchClient cloudWatchClient) {
        return new CloudWatchConnector(cloudWatchClient);
    }

    public double getTotalConsumedReadCapacityOfADynamoDbTable(
            final String tableName, final Instant startTime, final Instant endTime, final int periodInSeconds) {
        final var statistics = client.getMetricStatistics(GetMetricStatisticsRequest.builder()
                .metricName(CONSUMED_READ_CAPACITY_UNITS)
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

    public double getTotalConsumedWriteCapacityOfADynamoDbTable(
            final String tableName, final Instant startTime, final Instant endTime, final int periodInSeconds) {
        final var statistics = client.getMetricStatistics(GetMetricStatisticsRequest.builder()
                .metricName(CONSUMED_WRITE_CAPACITY_UNITS)
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
