package com.lyr.rule.dynamodb;

import static com.lyr.TestUtil.DUMMY_STRING;
import static com.lyr.TestUtil.TABLE_1;
import static com.lyr.TestUtil.TABLE_2;
import static com.lyr.TestUtil.TABLE_3;
import static com.lyr.TestUtil.TABLE_4;
import static com.lyr.TestUtil.TABLE_5;
import static com.lyr.TestUtil.TABLE_6;
import static com.lyr.TestUtil.createImmutableListOfFindings;
import static com.lyr.TestUtil.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutBackupRuleConfig;
import com.lyr.services.backup.BackupConnector;
import com.lyr.services.dynamodb.DynamoDbConnector;
import com.lyr.services.tag.TaggingConnector;
import com.lyr.services.util.ArnUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.backup.model.BackupSelection;
import software.amazon.awssdk.services.backup.model.Condition;
import software.amazon.awssdk.services.backup.model.ConditionType;
import software.amazon.awssdk.services.dynamodb.model.ContinuousBackupsDescription;
import software.amazon.awssdk.services.dynamodb.model.ContinuousBackupsStatus;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;

class ScanDynamodbTableWithoutBackupRuleExecutionTest {

    static MockedStatic<DynamoDbConnector> mockedDynamoDbConnector = Mockito.mockStatic(DynamoDbConnector.class);
    static DynamoDbConnector mockedDynamoDbConnectorInstance = Mockito.mock(DynamoDbConnector.class);
    static MockedStatic<TaggingConnector> mockedTaggingConnector = Mockito.mockStatic(TaggingConnector.class);
    static TaggingConnector mockedTaggingConnectorInstance = Mockito.mock(TaggingConnector.class);
    static MockedStatic<BackupConnector> mockedBackupConnector = Mockito.mockStatic(BackupConnector.class);
    static BackupConnector mockedBackupConnectorInstance = Mockito.mock(BackupConnector.class);
    static ScanDynamodbTableWithoutBackupRuleExecution testObject;

    @BeforeEach
    void beforeEach() {
        mockedDynamoDbConnector.when(DynamoDbConnector::create).thenReturn(mockedDynamoDbConnectorInstance);
        mockedTaggingConnector.when(TaggingConnector::create).thenReturn(mockedTaggingConnectorInstance);
        mockedBackupConnector.when(BackupConnector::create).thenReturn(mockedBackupConnectorInstance);
        testObject = new ScanDynamodbTableWithoutBackupRuleExecution();
    }

    @AfterEach
    void afterEach() {
        mockedDynamoDbConnector.reset();
        mockedTaggingConnector.reset();
        mockedBackupConnector.reset();
        reset(mockedDynamoDbConnectorInstance, mockedTaggingConnectorInstance, mockedBackupConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedDynamoDbConnector.closeOnDemand();
        mockedTaggingConnector.closeOnDemand();
        mockedBackupConnector.closeOnDemand();
    }

    @Test
    void testThatScanDynamodbTableWithoutBackupRuleExecutionWorksSuccessfullyWithOnlyScanningPITR() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(false)
                .passIfPitrEnabled(true)
                .build();

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_3);
        final var enabledContBackupDesc = ContinuousBackupsDescription.builder()
                .continuousBackupsStatus(ContinuousBackupsStatus.ENABLED)
                .build();
        final var disabledContBackupDesc = ContinuousBackupsDescription.builder()
                .continuousBackupsStatus(ContinuousBackupsStatus.DISABLED)
                .build();

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_1, TABLE_3)))
                .thenReturn(Optional.of(disabledContBackupDesc));
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_2, TABLE_4)))
                .thenReturn(Optional.of(enabledContBackupDesc));

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void testThatScanDynamodbTableWithoutBackupRuleExecutionWorksSuccessfullyWithOnlyScanningBackupPlan() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(false)
                .build();

        final var listOfBackupSelections = List.of(
                BackupSelection.builder()
                        .resources(tableNameToTableArn(TABLE_2), tableNameToTableArn(TABLE_4))
                        .build(),
                BackupSelection.builder()
                        .resources(tableNameToTableArn(TABLE_5))
                        .listOfTags(Condition.builder()
                                .conditionKey(DUMMY_STRING)
                                .conditionValue("enabled")
                                .conditionType(ConditionType.STRINGEQUALS)
                                .build())
                        .build());

        final var listOfResourceTagMappings = List.of(ResourceTagMapping.builder()
                .resourceARN(tableNameToTableArn(TABLE_6))
                .tags(Tag.builder().key(DUMMY_STRING).value("enabled").build())
                .build());

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4, TABLE_5, TABLE_6);

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_3);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(listOfResourceTagMappings);
        when(mockedBackupConnectorInstance.listAllBackupPlanSelections()).thenReturn(listOfBackupSelections);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void testThatScanDynamodbTableWithoutBackupRuleExecutionWorksSuccessfullyWithBothEnabled() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(true)
                .build();

        final var enabledContBackupDesc = ContinuousBackupsDescription.builder()
                .continuousBackupsStatus(ContinuousBackupsStatus.ENABLED)
                .build();
        final var disabledContBackupDesc = ContinuousBackupsDescription.builder()
                .continuousBackupsStatus(ContinuousBackupsStatus.DISABLED)
                .build();

        final var listOfBackupSelections = List.of(BackupSelection.builder()
                .resources(tableNameToTableArn(TABLE_4), tableNameToTableArn(TABLE_5))
                .listOfTags(Condition.builder()
                        .conditionKey(DUMMY_STRING)
                        .conditionValue("enabled")
                        .conditionType(ConditionType.STRINGEQUALS)
                        .build())
                .build());

        final var listOfResourceTagMappings = List.of(
                ResourceTagMapping.builder()
                        .resourceARN(tableNameToTableArn(TABLE_6))
                        .tags(Tag.builder().key(DUMMY_STRING).value("enabled").build())
                        .build(),
                ResourceTagMapping.builder()
                        .resourceARN(tableNameToTableArn(TABLE_3))
                        .tags(Tag.builder().key("BadKey").value("enabled").build())
                        .build());

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4, TABLE_5, TABLE_6);

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_3);

        // When
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_1, TABLE_3, TABLE_5, TABLE_6)))
                .thenReturn(Optional.of(disabledContBackupDesc));
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_2, TABLE_4)))
                .thenReturn(Optional.of(enabledContBackupDesc));
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(listOfResourceTagMappings);
        when(mockedBackupConnectorInstance.listAllBackupPlanSelections()).thenReturn(listOfBackupSelections);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void testThatScanDynamodbTableWithoutBackupRuleExecutionReturnsEmptyListWhenNoTablesArePresent() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(true)
                .build();
        final List<String> listOfTables = List.of();
        final var expectedFindings = ImmutableList.of();

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleWorksOnOnlyTheTablesThatExistsWhenOnlyPitrScanned() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(false)
                .passIfPitrEnabled(true)
                .build();

        final var tableNameThatDoesntExist = "tableThatDoesntExist";
        final var otherTableNameThatDoesntExists = "otherTableThatDoesntExists";

        final var listOfListTableResponses = List.of(
                TABLE_1, tableNameThatDoesntExist,
                otherTableNameThatDoesntExists, TABLE_4);

        final var disabledContBackupDesc = ContinuousBackupsDescription.builder()
                .continuousBackupsStatus(ContinuousBackupsStatus.DISABLED)
                .build();

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfListTableResponses);
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(
                        in(tableNameThatDoesntExist, otherTableNameThatDoesntExists)))
                .thenReturn(Optional.empty());
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_1, TABLE_4)))
                .thenReturn(Optional.of(disabledContBackupDesc));

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleWorksOnOnlyTheTablesThatExistsWhenOnlyBackupPlanScanned() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(false)
                .build();

        final var tableNameThatDoesntExist = "tableThatDoesntExist";
        final var otherTableNameThatDoesntExists = "otherTableThatDoesntExists";

        final var listOfListTableResponses = List.of(
                TABLE_1, tableNameThatDoesntExist,
                otherTableNameThatDoesntExists, TABLE_4);

        final var listOfBackupSelections = List.of(BackupSelection.builder()
                .resources(tableNameToTableArn(DUMMY_STRING))
                .build());

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfListTableResponses);
        when(mockedDynamoDbConnectorInstance.getTableDescription(in(TABLE_1, TABLE_4)))
                .thenAnswer(inv -> {
                    final String argument = inv.getArgument(0);
                    return createOptTableDescContArn(argument);
                });
        when(mockedDynamoDbConnectorInstance.getTableDescription(
                        in(tableNameThatDoesntExist, otherTableNameThatDoesntExists)))
                .thenReturn(Optional.empty());
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(List.of());
        when(mockedBackupConnectorInstance.listAllBackupPlanSelections()).thenReturn(listOfBackupSelections);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    private static Optional<TableDescription> createOptTableDescContArn(final String tableName) {
        return Optional.of(TableDescription.builder()
                .tableName(tableName)
                .tableArn(tableNameToTableArn(tableName))
                .build());
    }

    private static String tableNameToTableArn(final String tableName) {
        return ArnUtil.Arn.builder()
                .partition(DUMMY_STRING)
                .service(DUMMY_STRING)
                .region(DUMMY_STRING)
                .accountId(DUMMY_STRING)
                .resource(ArnUtil.Resource.builder().resourceId(tableName).build())
                .build()
                .toString();
    }
}
