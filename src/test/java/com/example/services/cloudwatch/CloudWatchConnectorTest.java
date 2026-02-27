package com.example.services.cloudwatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.example.TestUtil;
import com.example.services.ServiceProvider;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;

class CloudWatchConnectorTest {

    final CloudWatchClient mockedCloudWatchClient = mock(CloudWatchClient.class);
    CloudWatchConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = CloudWatchConnector.create(mockedCloudWatchClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedCloudWatchClient);
    }

    @Test
    void testThatCloudWatchConnectorGetsCloudWatchCientFromServiceProvider() {
        // Given
        try (var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            CloudWatchConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildCloudWatchClient, times(1));
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
}
