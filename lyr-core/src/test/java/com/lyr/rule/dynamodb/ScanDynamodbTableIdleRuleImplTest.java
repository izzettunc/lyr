package com.lyr.rule.dynamodb;

import static com.lyr.TestUtil.TABLE_1;
import static com.lyr.TestUtil.TABLE_2;
import static com.lyr.TestUtil.TABLE_3;
import static com.lyr.TestUtil.TABLE_4;
import static com.lyr.TestUtil.TABLE_WITH_DATA;
import static com.lyr.TestUtil.TABLE_WITH_DATA_OTHER;
import static com.lyr.TestUtil.createImmutableListOfFindings;
import static com.lyr.TestUtil.createOptionalDescribeTableResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import com.lyr.services.cloudwatch.CloudWatchUtil;
import com.lyr.services.dynamodb.DynamoDbConnector;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanDynamodbTableIdleRuleImplTest {

    static MockedStatic<CloudWatchUtil> mockedCloudWatchUtil = Mockito.mockStatic(CloudWatchUtil.class);
    static MockedStatic<CloudWatchConnector> mockedCloudWatchConnector = Mockito.mockStatic(CloudWatchConnector.class);
    static CloudWatchConnector mockedCloudWatchConnectorInstance = Mockito.mock(CloudWatchConnector.class);
    static MockedStatic<DynamoDbConnector> mockedDynamoDbConnector = Mockito.mockStatic(DynamoDbConnector.class);
    static DynamoDbConnector mockedDynamoDbConnectorInstance = Mockito.mock(DynamoDbConnector.class);
    static ScanDynamodbTableIdleRuleExecution testObject;

    private static final int VALID_MAX_IDLE_PERIOD_IN_DAYS = 5;

    @BeforeEach
    public void beforeEach() {
        mockedDynamoDbConnector.when(DynamoDbConnector::create).thenReturn(mockedDynamoDbConnectorInstance);
        mockedCloudWatchConnector.when(CloudWatchConnector::create).thenReturn(mockedCloudWatchConnectorInstance);
        testObject = new ScanDynamodbTableIdleRuleExecution();
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
        final var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(VALID_MAX_IDLE_PERIOD_IN_DAYS)
                .excludeEmptyTables(true)
                .build();
        final var timeWindow = 1;
        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);
        final var optTable = createOptionalDescribeTableResponse(123L);
        final var expectedOutcome = createImmutableListOfFindings(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTable(any())).thenReturn(optTable);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);

        final var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsEmptyListWhenNoTablesArePresent() {
        // Given
        final var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(VALID_MAX_IDLE_PERIOD_IN_DAYS)
                .excludeEmptyTables(true)
                .build();
        final var timeWindow = 1;
        final List<String> listOfTables = List.of();
        final var expectedOutcome = ImmutableList.of();

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);

        final var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsOnlyTheTablesThatHasZeroConsumedReadAndWriteCapacity() {
        // Given
        final var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(VALID_MAX_IDLE_PERIOD_IN_DAYS)
                .excludeEmptyTables(true)
                .build();
        final var timeWindow = 1;
        final var tableNameWithReadCon = "tableWithReadCon";
        final var tableNameWithWriteCon = "tableWithWriteCon";
        final var tableNameWithBothCon = "tableWithBothCon";
        final var dataSize = 123d;
        final var listOfTables =
                List.of(TABLE_1, tableNameWithReadCon, tableNameWithWriteCon, TABLE_4, tableNameWithBothCon);
        final var optTable = createOptionalDescribeTableResponse(123L);
        final var expectedOutcome = createImmutableListOfFindings(TABLE_1, TABLE_4);

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTable(any())).thenReturn(optTable);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(
                        or(eq(TABLE_1), eq(TABLE_4)), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(
                        or(eq(tableNameWithReadCon), eq(tableNameWithBothCon)), any(), any(), anyInt()))
                .thenReturn(dataSize);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(
                        or(eq(TABLE_1), eq(TABLE_4)), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(
                        or(eq(tableNameWithWriteCon), eq(tableNameWithBothCon)), any(), any(), anyInt()))
                .thenReturn(dataSize);

        final var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsOnlyTheTablesThatHasDataInIt() {
        // Given
        final var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(VALID_MAX_IDLE_PERIOD_IN_DAYS)
                .excludeEmptyTables(true)
                .build();
        final var timeWindow = 1;
        final var listOfTables = List.of(
                TABLE_1, TABLE_WITH_DATA,
                TABLE_WITH_DATA_OTHER, TABLE_4);
        final var optTableWithData = createOptionalDescribeTableResponse(123L);
        final var optTableWithNoData = createOptionalDescribeTableResponse(0L);
        final var expectedOutcome = createImmutableListOfFindings(TABLE_WITH_DATA, TABLE_WITH_DATA_OTHER);

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq(TABLE_WITH_DATA), eq(TABLE_WITH_DATA_OTHER))))
                .thenReturn(optTableWithData);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq(TABLE_1), eq(TABLE_4))))
                .thenReturn(optTableWithNoData);

        final var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleImplReturnsAllTheIdleTablesRegardlessTheirSize() {
        // Given
        final var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(VALID_MAX_IDLE_PERIOD_IN_DAYS)
                .excludeEmptyTables(false)
                .build();
        final var timeWindow = 1;
        final var listOfListTableResponses = List.of(
                TABLE_1, TABLE_WITH_DATA,
                TABLE_WITH_DATA_OTHER, TABLE_4);
        final var optTableWithData = createOptionalDescribeTableResponse(123L);
        final var optTableWithNoData = createOptionalDescribeTableResponse(0L);
        final var expectedOutcome =
                createImmutableListOfFindings(TABLE_1, TABLE_4, TABLE_WITH_DATA, TABLE_WITH_DATA_OTHER);

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfListTableResponses);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq(TABLE_WITH_DATA), eq(TABLE_WITH_DATA_OTHER))))
                .thenReturn(optTableWithData);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq(TABLE_1), eq(TABLE_4))))
                .thenReturn(optTableWithNoData);

        final var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleWorksOnOnlyTheTablesThatExists() {
        // Given
        final var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(VALID_MAX_IDLE_PERIOD_IN_DAYS)
                .excludeEmptyTables(false)
                .build();
        final var timeWindow = 1;
        final var tableNameThatDoesntExist = "tableThatDoesntExist";
        final var otherTableNameThatDoesntExists = "otherTableThatDoesntExists";
        final var listOfListTableResponses = List.of(
                TABLE_1, tableNameThatDoesntExist,
                otherTableNameThatDoesntExists, TABLE_4);
        final var optTable = createOptionalDescribeTableResponse(0L);
        final var expectedOutcome = createImmutableListOfFindings(TABLE_1, TABLE_4);

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfListTableResponses);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedReadCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedCloudWatchConnectorInstance.getTotalConsumedWriteCapacityOfADynamoDbTable(
                        any(), any(), any(), anyInt()))
                .thenReturn(0d);
        when(mockedDynamoDbConnectorInstance.getTable(or(eq(TABLE_WITH_DATA), eq(TABLE_WITH_DATA_OTHER))))
                .thenReturn(Optional.empty());
        when(mockedDynamoDbConnectorInstance.getTable(or(eq(TABLE_1), eq(TABLE_4))))
                .thenReturn(optTable);

        final var actualOutcome = testObject.execute(config);

        // Then
        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);
    }
}
