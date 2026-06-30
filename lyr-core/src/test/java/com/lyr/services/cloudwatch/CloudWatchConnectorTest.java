package com.lyr.services.cloudwatch;

import static com.lyr.TestUtil.LOG_GROUP_1;
import static com.lyr.TestUtil.LOG_GROUP_2;
import static com.lyr.TestUtil.LOG_GROUP_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.lyr.TestUtil;
import com.lyr.services.ServiceProvider;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;
import software.amazon.awssdk.services.cloudwatchlogs.paginators.DescribeLogGroupsIterable;

class CloudWatchConnectorTest {

    final CloudWatchClient mockedCloudWatchClient = mock(CloudWatchClient.class);
    final CloudWatchLogsClient mockedCloudWatchLogsClient = mock(CloudWatchLogsClient.class);
    CloudWatchConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = CloudWatchConnector.create(mockedCloudWatchClient, mockedCloudWatchLogsClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedCloudWatchClient);
    }

    @Test
    void testThatCloudWatchConnectorGetsCloudWatchClientsFromServiceProvider() {
        // Given
        try (var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            CloudWatchConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildCloudWatchClient, times(1));
            mockedServiceProvider.verify(ServiceProvider::getOrBuildCloudWatchLogsClient, times(1));
        }
    }

    @Test
    void testThatGetTotalConsumedReadCapacityOfADynamoDbTableReturnsSumOfAllDataPoints() {
        // Given
        final var metricStatisticDataPointValue = 3d;
        final var summedMetricStatisticDataPointValue = 12d;
        final var getMetricStatisticResponse = GetMetricStatisticsResponse.builder()
                .datapoints(
                        Datapoint.builder().sum(metricStatisticDataPointValue).build(),
                        Datapoint.builder().sum(metricStatisticDataPointValue).build(),
                        Datapoint.builder().sum(metricStatisticDataPointValue).build(),
                        Datapoint.builder().sum(metricStatisticDataPointValue).build())
                .build();

        // When
        when(mockedCloudWatchClient.getMetricStatistics(any(GetMetricStatisticsRequest.class)))
                .thenReturn(getMetricStatisticResponse);

        final var actualResult = testObject.getTotalConsumedReadCapacityOfADynamoDbTable(
                TestUtil.DUMMY_STRING, Instant.now(), Instant.now(), 123);

        // Then
        assertThat(actualResult).isEqualTo(summedMetricStatisticDataPointValue);
    }

    @Test
    void testThatGetTotalConsumedWriteCapacityOfADynamoDbTableReturnsSumOfAllDataPoints() {
        // Given
        final var metricStatisticDataPointValue = 3d;
        final var summedMetricStatisticDataPointValue = 12d;
        final var getMetricStatisticResponse = GetMetricStatisticsResponse.builder()
                .datapoints(
                        Datapoint.builder().sum(metricStatisticDataPointValue).build(),
                        Datapoint.builder().sum(metricStatisticDataPointValue).build(),
                        Datapoint.builder().sum(metricStatisticDataPointValue).build(),
                        Datapoint.builder().sum(metricStatisticDataPointValue).build())
                .build();

        // When
        when(mockedCloudWatchClient.getMetricStatistics(any(GetMetricStatisticsRequest.class)))
                .thenReturn(getMetricStatisticResponse);

        final var actualResult = testObject.getTotalConsumedWriteCapacityOfADynamoDbTable(
                TestUtil.DUMMY_STRING, Instant.now(), Instant.now(), 123);

        // Then
        assertThat(actualResult).isEqualTo(summedMetricStatisticDataPointValue);
    }

    @Test
    void testThatListLogGroupDetailsReturnsListOfLogGroupDetails() {
        // Given
        final var logGroup1 = LogGroup.builder().logGroupName(LOG_GROUP_1).build();
        final var logGroup2 = LogGroup.builder().logGroupName(LOG_GROUP_2).build();
        final var logGroup3 = LogGroup.builder().logGroupName(LOG_GROUP_3).build();

        final var listOfDescribeLogGroupsResponse = List.of(
                DescribeLogGroupsResponse.builder().logGroups(logGroup2).build(),
                DescribeLogGroupsResponse.builder()
                        .logGroups(logGroup1, logGroup3)
                        .build());

        final var mockedDescribeLogGroupsIterable = mock(DescribeLogGroupsIterable.class);

        final var expectedListOfLogGroups = List.of(logGroup1, logGroup2, logGroup3);

        // When
        when(mockedDescribeLogGroupsIterable.stream()).thenReturn(listOfDescribeLogGroupsResponse.stream());
        when(mockedCloudWatchLogsClient.describeLogGroupsPaginator()).thenReturn(mockedDescribeLogGroupsIterable);
        final var actualResult = testObject.listLogGroupDetails();

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedListOfLogGroups);
    }
}
