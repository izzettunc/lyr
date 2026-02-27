package com.example.services.dynamodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.example.TestUtil;
import com.example.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.paginators.ListTablesIterable;

class DynamoDbConnectorTest {

    final DynamoDbClient mockedDynamoDbClient = Mockito.mock(DynamoDbClient.class);
    DynamoDbConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = DynamoDbConnector.create(mockedDynamoDbClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedDynamoDbClient);
    }

    @Test
    void testThatDynamoDbConnectorGetsDynamoDbClientFromServiceProvider() {
        // Given
        try (final var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            DynamoDbConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildDynamoDbClient, times(1));
        }
    }

    @Test
    void testThatGetTableReturnsOptionalOfDescribeTableResponseWhenFound() {
        // Given
        final var expectedDescribeTableResponse = DescribeTableResponse.builder()
                .table(TableDescription.builder()
                        .tableName(TestUtil.DUMMY_STRING)
                        .build())
                .build();

        // When
        when(mockedDynamoDbClient.describeTable(any(DescribeTableRequest.class)))
                .thenReturn(expectedDescribeTableResponse);
        final var actualResult = testObject.getTable(TestUtil.DUMMY_STRING);

        // Then
        assertThat(actualResult).isEqualTo(Optional.of(expectedDescribeTableResponse));
    }

    @Test
    void testThatGetParameterReturnsOptionalOfDescribeTableResponseWhenNotFound() {
        // Given nothing
        // When
        when(mockedDynamoDbClient.describeTable(any(DescribeTableRequest.class)))
                .thenThrow(ResourceNotFoundException.class);
        final var actualResult = testObject.getTable(TestUtil.DUMMY_STRING);

        // Then
        assertThat(actualResult).isEqualTo(Optional.empty());
    }

    @Test
    void testThatListTablesReturnsListOfListTablesResponse() {
        // Given
        final var listOfListTablesResponse = List.of(
                ListTablesResponse.builder().tableNames("table1", "table2").build(),
                ListTablesResponse.builder().tableNames("table3", "table4").build());

        final var mockedListTablesIterable = mock(ListTablesIterable.class);

        // When
        when(mockedListTablesIterable.stream()).thenReturn(listOfListTablesResponse.stream());
        when(mockedDynamoDbClient.listTablesPaginator()).thenReturn(mockedListTablesIterable);

        final var actualResult = testObject.listTables();

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(listOfListTablesResponse);
    }
}
