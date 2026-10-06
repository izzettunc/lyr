package com.lyr.rule.dynamodb;

import static com.lyr.TestUtil.TABLE_1;
import static com.lyr.TestUtil.TABLE_2;
import static com.lyr.TestUtil.TABLE_3;
import static com.lyr.TestUtil.TABLE_4;
import static com.lyr.TestUtil.createImmutableListOfFindings;
import static com.lyr.TestUtil.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutDeletionProtectionRuleConfig;
import com.lyr.services.dynamodb.DynamoDbConnector;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

class ScanDynamodbTableWithoutDeletionProtectionRuleExecutionTest {
    static MockedStatic<DynamoDbConnector> mockedDynamoDbConnector = Mockito.mockStatic(DynamoDbConnector.class);
    static DynamoDbConnector mockedDynamoDbConnectorInstance = Mockito.mock(DynamoDbConnector.class);
    static ScanDynamodbTableWithoutDeletionProtectionRuleExecution testObject;

    @BeforeEach
    public void beforeEach() {
        mockedDynamoDbConnector.when(DynamoDbConnector::create).thenReturn(mockedDynamoDbConnectorInstance);
        testObject = new ScanDynamodbTableWithoutDeletionProtectionRuleExecution();
    }

    @AfterEach
    void afterEach() {
        mockedDynamoDbConnector.reset();
        reset(mockedDynamoDbConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedDynamoDbConnector.closeOnDemand();
    }

    @Test
    void testThatRuleExecutesSuccessfully() {
        // Given
        final var config =
                ScanDynamodbTableWithoutDeletionProtectionRuleConfig.builder().build();

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(anyString())).thenAnswer(inv -> {
            final String tableName = inv.getArgument(0);
            return createOptTableDescription(tableName);
        });

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult).isEmpty();
    }

    @Test
    void testThatRuleReturnsOnlyTablesWithoutDeletionProtection() {
        // Given
        final var config =
                ScanDynamodbTableWithoutDeletionProtectionRuleConfig.builder().build();

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        final var expectedFindings = createImmutableListOfFindings(TABLE_1, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(in(TABLE_2, TABLE_3)))
                .thenAnswer(inv -> {
                    final String tableName = inv.getArgument(0);
                    return createOptTableDescription(tableName);
                });
        when(mockedDynamoDbConnectorInstance.getTableDescription(in(TABLE_1, TABLE_4)))
                .thenAnswer(inv -> {
                    final String tableName = inv.getArgument(0);
                    return createOptTableDescription(tableName, false);
                });

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(expectedFindings);
    }

    @Test
    void testThatRuleReturnsEmptyListWhenTablesAreNotPresent() {
        // Given
        final var config =
                ScanDynamodbTableWithoutDeletionProtectionRuleConfig.builder().build();

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(List.of());

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult).isEmpty();
    }

    @Test
    void testThatRuleReturnsEmptyListWhenTableDescsAreNotPresent() {
        // Given
        final var config =
                ScanDynamodbTableWithoutDeletionProtectionRuleConfig.builder().build();

        final var listOfTables = List.of(TABLE_1, TABLE_2, TABLE_3, TABLE_4);

        // When
        when(mockedDynamoDbConnectorInstance.listTableNames()).thenReturn(listOfTables);
        when(mockedDynamoDbConnectorInstance.getTableDescription(any())).thenReturn(Optional.empty());

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult).isEmpty();
    }

    private Optional<TableDescription> createOptTableDescription(final String tableName) {
        return createOptTableDescription(tableName, true);
    }

    private Optional<TableDescription> createOptTableDescription(
            final String tableName, final boolean deletionProtectionEnabled) {
        return Optional.of(TableDescription.builder()
                .tableName(tableName)
                .deletionProtectionEnabled(deletionProtectionEnabled)
                .build());
    }
}
