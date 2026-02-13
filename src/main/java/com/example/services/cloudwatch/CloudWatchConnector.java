package com.example.services.cloudwatch;

import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;

import java.time.Instant;

public class CloudWatchConnector {
    private static final String AWS_DYNAMO_DB = "AWS/DynamoDB";
    private static final String CONSUMED_READ_CAPACITY_UNITS = "ConsumedReadCapacityUnits";
    private static final String CONSUMED_WRITE_CAPACITY_UNITS = "ConsumedWriteCapacityUnits";
    private static final String TABLE_NAME = "TableName";

    private static CloudWatchClient client;
    private static CloudWatchConnector instance;

    private CloudWatchConnector() {
        client = buildClient();
    }

    public static CloudWatchConnector getInstance() {
        if (instance == null) {
            instance = new CloudWatchConnector();
        }

        return instance;
    }

    public double getTotalConsumedReadCapacityOfADynamoDbTable(String tableName, Instant from, Instant to, int periodInSeconds) {
        var statistics = client.getMetricStatistics(GetMetricStatisticsRequest.builder()
                        .metricName(CONSUMED_READ_CAPACITY_UNITS)
                        .startTime(from)
                        .endTime(to)
                        .period(periodInSeconds)
                        .namespace(AWS_DYNAMO_DB)
                        .statistics(Statistic.SUM)
                        .dimensions(Dimension.builder()
                                .name(TABLE_NAME)
                                .value(tableName)
                                .build())
                .build());

        return statistics.datapoints().stream().map(Datapoint::sum).reduce(Double::sum).orElse(0d);
    }

     public double getTotalConsumedWriteCapacityOfADynamoDbTable(String tableName, Instant from, Instant to, int periodInSeconds) {
        var statistics = client.getMetricStatistics(GetMetricStatisticsRequest.builder()
                .metricName(CONSUMED_WRITE_CAPACITY_UNITS)
                .startTime(from)
                .endTime(to)
                .period(periodInSeconds)
                .namespace(AWS_DYNAMO_DB)
                .statistics(Statistic.SUM)
                .dimensions(Dimension.builder()
                        .name(TABLE_NAME)
                        .value(tableName)
                        .build())
                .build());

         return statistics.datapoints().stream().map(Datapoint::sum).reduce(Double::sum).orElse(0d);
    }

    private static CloudWatchClient buildClient() {
        if (!StringUtils.isBlank(System.getenv(SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable()))) {
            return CloudWatchClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return CloudWatchClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }
}
