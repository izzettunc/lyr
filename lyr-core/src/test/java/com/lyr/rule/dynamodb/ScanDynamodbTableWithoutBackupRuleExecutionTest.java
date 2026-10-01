package com.lyr.rule.dynamodb;

import static com.lyr.TestUtil.DUMMY2_STRING;
import static com.lyr.TestUtil.DUMMY3_STRING;
import static com.lyr.TestUtil.DUMMY4_STRING;
import static com.lyr.TestUtil.DUMMY_STRING;
import static com.lyr.TestUtil.TABLE_1;
import static com.lyr.TestUtil.TABLE_2;
import static com.lyr.TestUtil.TABLE_3;
import static com.lyr.TestUtil.TABLE_4;
import static com.lyr.TestUtil.TABLE_5;
import static com.lyr.TestUtil.TABLE_6;
import static com.lyr.TestUtil.TABLE_7;
import static com.lyr.TestUtil.WILDCARD_SYMBOL;
import static com.lyr.TestUtil.createImmutableListOfFindings;
import static com.lyr.TestUtil.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutBackupRuleConfig;
import com.lyr.services.backup.BackupConnector;
import com.lyr.services.dynamodb.DynamoDbConnector;
import com.lyr.services.tag.TaggingConnector;
import com.lyr.services.util.ArnUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.backup.model.BackupSelection;
import software.amazon.awssdk.services.backup.model.ConditionParameter;
import software.amazon.awssdk.services.backup.model.Conditions;
import software.amazon.awssdk.services.dynamodb.model.ContinuousBackupsDescription;
import software.amazon.awssdk.services.dynamodb.model.PointInTimeRecoveryDescription;
import software.amazon.awssdk.services.dynamodb.model.PointInTimeRecoveryStatus;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;

class ScanDynamodbTableWithoutBackupRuleExecutionTest {

    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

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

        final var enabledContBackupDesc = createContinuousBackupDescription(PointInTimeRecoveryStatus.ENABLED);
        final var disabledContBackupDesc = createContinuousBackupDescription(PointInTimeRecoveryStatus.DISABLED);
        final var nullContBackupDesc = createContinuousBackupDescription(null);

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4, TABLE_5);

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_3, TABLE_5);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_1, TABLE_3)))
                .thenReturn(Optional.of(disabledContBackupDesc));
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_2, TABLE_4)))
                .thenReturn(Optional.of(enabledContBackupDesc));
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(eq(TABLE_5)))
                .thenReturn(Optional.of(nullContBackupDesc));

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void
            testThatScanDynamodbTableWithoutBackupRuleExecutionWorksSuccessfullyWithOnlyScanningBackupPlanWhenBackupSelectionCoversResources() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(false)
                .build();

        final var listOfBackupSelections = List.of(
                createBackupSelection(List.of(tableNameToTableArn(TABLE_2), tableNameToTableArn(TABLE_4)), List.of()),
                createBackupSelection(List.of(tableNameToTableArn(TABLE_3)), List.of()));

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        final var expectedFindings = createImmutableListOfFindings(TABLE_1);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(emptyMapOfTagsFor(listOfTables));
        when(mockedBackupConnectorInstance.listAllBackupPlanSelections()).thenReturn(listOfBackupSelections);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void
            testThatScanDynamodbTableWithoutBackupRuleExecutionWorksSuccessfullyWithOnlyScanningBackupPlanWhenBackupSelectionCoversResourcesWithWildcard() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(false)
                .build();

        final var listOfBackupSelections = List.of(createBackupSelection(List.of(WILDCARD_SYMBOL), List.of()));

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        final var expectedFindings = createImmutableListOfFindings();

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(emptyMapOfTagsFor(listOfTables));
        when(mockedBackupConnectorInstance.listAllBackupPlanSelections()).thenReturn(listOfBackupSelections);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void
            testThatScanDynamodbTableWithoutBackupRuleExecutionWorksSuccessfullyWithOnlyScanningBackupPlanWhenBackupSelectionCoversTags() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(false)
                .build();

        final var listOfBackupSelections = List.of(
                createBackupSelection(List.of(), List.of(createConditionParam(DUMMY_STRING, ENABLED))),
                createBackupSelection(
                        List.of(),
                        List.of(
                                createConditionParam(DUMMY3_STRING, ENABLED),
                                createConditionParam(DUMMY4_STRING, ENABLED))));

        final Map<String, List<Tag>> mapOfTags = Map.of(
                tableNameToTableArn(TABLE_1),
                List.of(createTag(DUMMY_STRING, ENABLED)),
                tableNameToTableArn(TABLE_2),
                List.of(createTag(DUMMY2_STRING, ENABLED)),
                tableNameToTableArn(TABLE_3),
                List.of(createTag(DUMMY_STRING, DISABLED)),
                tableNameToTableArn(TABLE_4),
                List.of(
                        createTag(DUMMY2_STRING, ENABLED),
                        createTag(DUMMY3_STRING, ENABLED),
                        createTag(DUMMY4_STRING, ENABLED)),
                tableNameToTableArn(TABLE_5),
                List.of(createTag(DUMMY3_STRING, ENABLED)));

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4, TABLE_5);

        final var expectedFindings = createImmutableListOfFindings(TABLE_2, TABLE_3, TABLE_5);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(mapOfTags);
        when(mockedBackupConnectorInstance.listAllBackupPlanSelections()).thenReturn(listOfBackupSelections);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void
            testThatScanDynamodbTableWithoutBackupRuleExecutionWorksSuccessfullyWithOnlyScanningBackupPlanWhenBackupSelectionCoversTagsAndResources() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(false)
                .build();

        final var listOfBackupSelections = List.of(createBackupSelection(
                List.of(tableNameToTableArn(TABLE_2), tableNameToTableArn(TABLE_4), tableNameToTableArn(TABLE_3)),
                List.of(createConditionParam(DUMMY_STRING, ENABLED))));

        final Map<String, List<Tag>> mapOfTags = Map.of(
                tableNameToTableArn(TABLE_1), List.of(createTag(DUMMY_STRING, ENABLED)),
                tableNameToTableArn(TABLE_2), List.of(createTag(DUMMY_STRING, ENABLED)),
                tableNameToTableArn(TABLE_3), List.of(),
                tableNameToTableArn(TABLE_4), List.of(createTag(DUMMY_STRING, DISABLED)));

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_3, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(mapOfTags);
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

        final var enabledContBackupDesc = createContinuousBackupDescription(PointInTimeRecoveryStatus.ENABLED);
        final var disabledContBackupDesc = createContinuousBackupDescription(PointInTimeRecoveryStatus.DISABLED);

        final var listOfBackupSelections = List.of(createBackupSelection(
                List.of(
                        tableNameToTableArn(TABLE_4),
                        tableNameToTableArn(TABLE_3),
                        tableNameToTableArn(TABLE_5),
                        tableNameToTableArn(TABLE_7)),
                List.of(createConditionParam(DUMMY_STRING, ENABLED))));

        final Map<String, List<Tag>> mapOfTags = Map.of(
                tableNameToTableArn(TABLE_1), List.of(),
                tableNameToTableArn(TABLE_2), List.of(),
                tableNameToTableArn(TABLE_3), List.of(createTag(DUMMY2_STRING, ENABLED)),
                tableNameToTableArn(TABLE_4), List.of(createTag(DUMMY_STRING, ENABLED)),
                tableNameToTableArn(TABLE_5), List.of(),
                tableNameToTableArn(TABLE_6), List.of(createTag(DUMMY_STRING, ENABLED)),
                tableNameToTableArn(TABLE_7), List.of(createTag(DUMMY_STRING, ENABLED)));

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4, TABLE_5, TABLE_6, TABLE_7);

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_3, TABLE_5, TABLE_6);

        // When
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(
                        in(TABLE_1, TABLE_3, TABLE_5, TABLE_6, TABLE_7)))
                .thenReturn(Optional.of(disabledContBackupDesc));
        when(mockedDynamoDbConnectorInstance.getContinuousBackupsDescription(in(TABLE_2, TABLE_4)))
                .thenReturn(Optional.of(enabledContBackupDesc));
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(mapOfTags);
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

        verify(mockedDynamoDbConnectorInstance, never()).getContinuousBackupsDescription(anyString());
        verify(mockedBackupConnectorInstance, never()).listAllBackupPlanSelections();
        verify(mockedTaggingConnectorInstance, never()).getResourceTagMappingForResources(anyString());
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

        final var disabledContBackupDesc = createContinuousBackupDescription(PointInTimeRecoveryStatus.DISABLED);

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

        final var listOfTables = List.of(
                TABLE_1, tableNameThatDoesntExist,
                otherTableNameThatDoesntExists, TABLE_4);

        final var listOfBackupSelections = List.of(BackupSelection.builder()
                .resources(tableNameToTableArn(DUMMY_STRING))
                .build());

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(in(TABLE_1, TABLE_4)))
                .thenAnswer(inv -> {
                    final String argument = inv.getArgument(0);
                    return createOptTableDescContArn(argument);
                });
        when(mockedDynamoDbConnectorInstance.getTableDescription(
                        in(tableNameThatDoesntExist, otherTableNameThatDoesntExists)))
                .thenReturn(Optional.empty());
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(emptyMapOfTagsFor(listOfTables));
        when(mockedBackupConnectorInstance.listAllBackupPlanSelections()).thenReturn(listOfBackupSelections);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleGracefullyIgnoresUnsupportedConditionsWhenOnlyBackupPlanScanned() {
        // Given
        final var config = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(true)
                .passIfPitrEnabled(false)
                .build();

        final var listOfBackupSelections = List.of(
                BackupSelection.builder()
                        .resources(tableNameToTableArn(TABLE_1))
                        .conditions(Conditions.builder()
                                .stringNotEquals(ConditionParameter.builder()
                                        .conditionKey(DUMMY_STRING)
                                        .conditionValue(ENABLED)
                                        .build())
                                .build())
                        .build(),
                BackupSelection.builder()
                        .conditions(Conditions.builder()
                                .stringNotEquals(ConditionParameter.builder()
                                        .conditionKey(DUMMY_STRING)
                                        .conditionValue(ENABLED)
                                        .build())
                                .build())
                        .build(),
                BackupSelection.builder()
                        .notResources(tableNameToTableArn(TABLE_3))
                        .build());

        final Map<String, List<Tag>> mapOfTags = Map.of(
                tableNameToTableArn(TABLE_1), List.of(createTag(DUMMY_STRING, ENABLED)),
                tableNameToTableArn(TABLE_2), List.of(createTag(DUMMY_STRING, ENABLED)),
                tableNameToTableArn(TABLE_3), List.of());

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3);

        final var expectedFindings = createImmutableListOfFindings(TABLE_2, TABLE_3);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String argument = inv.getArgument(0);
            return createOptTableDescContArn(argument);
        });
        when(mockedTaggingConnectorInstance.getResourceTagMappingForResources(any(String[].class)))
                .thenReturn(mapOfTags);
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

    private static ContinuousBackupsDescription createContinuousBackupDescription(
            final PointInTimeRecoveryStatus pointInTimeRecoveryStatus) {
        final var baseBuilder = ContinuousBackupsDescription.builder();

        if (null == pointInTimeRecoveryStatus) {
            return baseBuilder.build();
        }

        return baseBuilder
                .pointInTimeRecoveryDescription(PointInTimeRecoveryDescription.builder()
                        .pointInTimeRecoveryStatus(pointInTimeRecoveryStatus)
                        .build())
                .build();
    }

    private static Tag createTag(final String key, final String value) {
        return Tag.builder().key(key).value(value).build();
    }

    private static ConditionParameter createConditionParam(final String key, final String value) {
        return ConditionParameter.builder()
                .conditionKey(key)
                .conditionValue(value)
                .build();
    }

    private static BackupSelection createBackupSelection(
            final List<String> resourcePatternList, final List<ConditionParameter> conditionParameterList) {
        final var builder = BackupSelection.builder();

        if (!resourcePatternList.isEmpty()) {
            builder.resources(resourcePatternList);
        }

        if (!conditionParameterList.isEmpty()) {
            builder.conditions(
                    Conditions.builder().stringEquals(conditionParameterList).build());
        }

        return builder.build();
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

    private static Map<String, List<Tag>> emptyMapOfTagsFor(final List<String> arns) {
        return arns.stream().collect(Collectors.toMap(arn -> arn, _ -> List.of()));
    }
}
