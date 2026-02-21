package com.example.rule.dynamodb;

import com.example.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.example.services.cloudwatch.CloudWatchConnector;
import com.example.services.cloudwatch.CloudWatchUtil;
import com.example.services.dynamodb.DynamoDbConnector;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import java.util.List;
import java.util.Optional;

import static com.example.TestUtil.createDummyListTableResponse;
import static com.example.TestUtil.createImmutableListOfScanOutcome;
import static com.example.TestUtil.createOptionalDescribeTableResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class ScanDynamodbTableIdleRuleImplTest {

    static MockedStatic<CloudWatchUtil> mockedCloudWatchUtil = Mockito.mockStatic(CloudWatchUtil.class);
    static MockedStatic<CloudWatchConnector> mockedCloudWatchConnector = Mockito.mockStatic(CloudWatchConnector.class);
    static CloudWatchConnector mockedCloudWatchConnectorInstance = Mockito.mock(CloudWatchConnector.class);
    static MockedStatic<DynamoDbConnector> mockedDynamoDbConnector = Mockito.mockStatic(DynamoDbConnector.class);
    static DynamoDbConnector mockedDynamoDbConnectorInstance = Mockito.mock(DynamoDbConnector.class);
    static ScanDynamodbTableIdleRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedDynamoDbConnector.when(DynamoDbConnector::create).thenReturn(mockedDynamoDbConnectorInstance);
        mockedCloudWatchConnector.when(CloudWatchConnector::create).thenReturn(mockedCloudWatchConnectorInstance);
        testObject = new ScanDynamodbTableIdleRuleImpl();
    }

    @AfterEach
    void afterEach() {
        mockedDynamoDbConnector.reset();
        mockedCloudWatchConnector.reset();
        mockedCloudWatchUtil.reset();
        reset(mockedDynamoDbConnectorInstance, mockedCloudWatchConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedDynamoDbConnector.closeOnDemand();
        mockedCloudWatchConnector.closeOnDemand();
        mockedCloudWatchUtil.closeOnDemand();
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplExecutesSuccessfully() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(5).excludeEmptyTables(true).build();
        var timeWindow = 1;
        var listOfListTableResponses = List.of(
                createDummyListTableResponse("table1", "table2"),
                createDummyListTableResponse("table3", "table4"));
        var optTable = createOptionalDescribeTableResponse(123L);
        var expectedOutcome = createImmutableListOfScanOutcome("table1", "table2", "table3", "table4");

        // When
        mockedCloudWatchUtil.when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt())).thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTables()).thenReturn(listOfListTableResponses);
        when(mockedDynamoDbConnectorInstance.getTable(any())).thenReturn(optTable);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);

        var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }


    @Test
    void testThatScanDynamodbTableIdleRuleImplThrowsIllegalArgumentExceptionWhenLessThanADayProvidedAsAPeriod() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(-5).excludeEmptyTables(true).build();

        // When & Then
        assertThatThrownBy(() -> testObject.execute(config)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsEmptyListWhenNoTablesArePresent() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(5).excludeEmptyTables(true).build();
        var timeWindow = 1;
        var listOfListTableResponses = List.of(ListTablesResponse.builder().build());
        var expectedOutcome = ImmutableList.of();

        // When
        mockedCloudWatchUtil.when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt())).thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTables()).thenReturn(listOfListTableResponses);

        var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsOnlyTheTablesThatHasZeroConsumedReadAndWriteCapacity() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(5).excludeEmptyTables(true).build();
        var timeWindow = 1;
        var listOfListTableResponses = List.of(
                createDummyListTableResponse("table1", "tableWithReadCon"),
                createDummyListTableResponse("tableWithWriteCon", "table4", "tableWithBothCon"));
        var optTable = createOptionalDescribeTableResponse(123L);
        var expectedOutcome = createImmutableListOfScanOutcome("table1", "table4");

        // When
        mockedCloudWatchUtil.when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt())).thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTables()).thenReturn(listOfListTableResponses);
        when(mockedDynamoDbConnectorInstance.getTable(any())).thenReturn(optTable);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(or(eq("table1"), eq("table4")), any(), any(), anyInt())).thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(or(eq("tableWithReadCon"), eq("tableWithBothCon")), any(), any(), anyInt())).thenReturn(123d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(or(eq("table1"), eq("table4")), any(), any(), anyInt())).thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(or(eq("tableWithWriteCon"), eq("tableWithBothCon")), any(), any(), anyInt())).thenReturn(123d);

        var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsOnlyTheTablesThatHasDataInIt() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(5).excludeEmptyTables(true).build();
        var timeWindow = 1;
        var listOfListTableResponses = List.of(
                createDummyListTableResponse("table1", "tableWithData"),
                createDummyListTableResponse("tableWithDataOther", "table4"));
        var optTableWithData = createOptionalDescribeTableResponse(123L);
        var optTableWithNoData = createOptionalDescribeTableResponse(0L);
        var expectedOutcome = createImmutableListOfScanOutcome("tableWithData", "tableWithDataOther");

        // When
        mockedCloudWatchUtil.when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt())).thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTables()).thenReturn(listOfListTableResponses);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq("tableWithData"), eq("tableWithDataOther")))).thenReturn(optTableWithData);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq("table1"), eq("table4")))).thenReturn(optTableWithNoData);

        var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsAllTheIdleTablesRegardlessTheirSize() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(5).excludeEmptyTables(false).build();
        var timeWindow = 1;
        var listOfListTableResponses = List.of(
                createDummyListTableResponse("table1", "tableWithData"),
                createDummyListTableResponse("tableWithDataOther", "table4"));
        var optTableWithData = createOptionalDescribeTableResponse(123L);
        var optTableWithNoData = createOptionalDescribeTableResponse(0L);
        var expectedOutcome = createImmutableListOfScanOutcome("table1", "table4", "tableWithData", "tableWithDataOther");

        // When
        mockedCloudWatchUtil.when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt())).thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTables()).thenReturn(listOfListTableResponses);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq("tableWithData"), eq("tableWithDataOther")))).thenReturn(optTableWithData);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq("table1"), eq("table4")))).thenReturn(optTableWithNoData);

        var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleWorksOnOnlyTheTablesThatExists() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(5).excludeEmptyTables(false).build();
        var timeWindow = 1;
        var listOfListTableResponses = List.of(
                createDummyListTableResponse("table1", "tableThatDoesntExist"),
                createDummyListTableResponse("otherTableThatDoesntExists", "table4"));
        var optTable = createOptionalDescribeTableResponse(0L);
        var expectedOutcome = createImmutableListOfScanOutcome("table1", "table4");

        // When
        mockedCloudWatchUtil.when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt())).thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTables()).thenReturn(listOfListTableResponses);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(any(), any(), any(), anyInt())).thenReturn(0d);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq("tableWithData"), eq("tableWithDataOther")))).thenReturn(Optional.empty());
        when(mockedDynamoDbConnectorInstance.getTable(or(eq("table1"), eq("table4")))).thenReturn(optTable);

        var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

}
