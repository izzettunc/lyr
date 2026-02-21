package com.example.services.cloudwatch;


import com.example.services.ServiceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class CloudWatchConnectorTest {

    CloudWatchClient mockedCloudWatchClient = mock(CloudWatchClient.class);
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
        var getMetricStatisticResponse = GetMetricStatisticsResponse.builder().datapoints(
                        Datapoint.builder().sum(1d).build(),
                        Datapoint.builder().sum(2d).build(),
                        Datapoint.builder().sum(3d).build(),
                        Datapoint.builder().sum(4d).build())
                .build();

        // When
        when(mockedCloudWatchClient.getMetricStatistics(any(GetMetricStatisticsRequest.class)))
                .thenReturn(getMetricStatisticResponse);

        var actualResult = testObject.getTotalConsumedReadCapacityOfADynamoDbTable("dummy", Instant.now(), Instant.now(), 123);

        // Then
        assertThat(actualResult).isEqualTo(10d);
    }

    @Test
    void testThatGetTotalConsumedWriteCapacityOfADynamoDbTableReturnsSumOfAllDataPoints() {
        // Given
        var getMetricStatisticResponse = GetMetricStatisticsResponse.builder().datapoints(
                        Datapoint.builder().sum(1d).build(),
                        Datapoint.builder().sum(2d).build(),
                        Datapoint.builder().sum(3d).build(),
                        Datapoint.builder().sum(4d).build())
                .build();

        // When
        when(mockedCloudWatchClient.getMetricStatistics(any(GetMetricStatisticsRequest.class)))
                .thenReturn(getMetricStatisticResponse);

        var actualResult = testObject.getTotalConsumedWriteCapacityOfADynamoDbTable("dummy", Instant.now(), Instant.now(), 123);

        // Then
        assertThat(actualResult).isEqualTo(10d);
    }
}