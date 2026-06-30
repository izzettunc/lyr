package com.lyr.rule.cloudwatch;

import static com.lyr.TestUtil.LOG_GROUP_1;
import static com.lyr.TestUtil.LOG_GROUP_2;
import static com.lyr.TestUtil.LOG_GROUP_3;
import static com.lyr.TestUtil.LOG_GROUP_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecutionTest {

    static MockedStatic<CloudWatchConnector> mockedCloudWatchConnector = Mockito.mockStatic(CloudWatchConnector.class);
    static CloudWatchConnector mockedCloudWatchConnectorInstance = Mockito.mock(CloudWatchConnector.class);
    static ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution testObject;

    @BeforeEach
    public void beforeEach() {
        mockedCloudWatchConnector.when(CloudWatchConnector::create).thenReturn(mockedCloudWatchConnectorInstance);
        testObject = new ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution();
    }

    @AfterEach
    void afterEach() {
        mockedCloudWatchConnector.reset();
        reset(mockedCloudWatchConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedCloudWatchConnector.closeOnDemand();
    }

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecutesSuccessfully() {
        // Given
        final var config =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        final var listOfLogGroupDescriptions = List.of(
                createLogGroup(LOG_GROUP_1),
                createLogGroup(LOG_GROUP_2),
                createLogGroup(LOG_GROUP_3),
                createLogGroup(LOG_GROUP_4));

        final var expectedResult = Stream.of(LOG_GROUP_1, LOG_GROUP_2, LOG_GROUP_3, LOG_GROUP_4)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedCloudWatchConnectorInstance.listLogGroupDetails()).thenReturn(listOfLogGroupDescriptions);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleReturnsOnlyTheLogGroupsWithoutRetentionPolicy() {
        // Given
        final var config =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        final var listOfLogGroupDescriptions = List.of(
                createLogGroup(LOG_GROUP_1),
                createLogGroup(LOG_GROUP_2, 123),
                createLogGroup(LOG_GROUP_3, 456),
                createLogGroup(LOG_GROUP_4));

        final var expectedResult =
                Stream.of(LOG_GROUP_1, LOG_GROUP_4).map(Finding::byId).collect(ImmutableList.toImmutableList());

        // When
        when(mockedCloudWatchConnectorInstance.listLogGroupDetails()).thenReturn(listOfLogGroupDescriptions);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleReturnsEmptyListWhenNoLogGroupArePresent() {
        // Given
        final var config =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        final List<LogGroup> listOfLogGroupDescriptions = List.of();

        final var expectedResult = ImmutableList.of();

        // When
        when(mockedCloudWatchConnectorInstance.listLogGroupDetails()).thenReturn(listOfLogGroupDescriptions);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    private static LogGroup createLogGroup(final String logGroupName, final Integer retentionInDays) {
        return LogGroup.builder()
                .logGroupName(logGroupName)
                .retentionInDays(retentionInDays)
                .build();
    }

    private static LogGroup createLogGroup(final String logGroupName) {
        return createLogGroup(logGroupName, null);
    }
}
