package com.lyr.services.dynamodb;

import com.lyr.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ContinuousBackupsDescription;
import software.amazon.awssdk.services.dynamodb.model.DescribeContinuousBackupsRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

public final class DynamoDbConnector {

    private final DynamoDbClient client;

    private DynamoDbConnector(final DynamoDbClient dynamoDbClient) {
        this.client = dynamoDbClient;
    }

    public static DynamoDbConnector create() {
        return new DynamoDbConnector(ServiceProvider.getOrBuildDynamoDbClient());
    }

    public static DynamoDbConnector create(final DynamoDbClient dynamoDbClient) {
        return new DynamoDbConnector(dynamoDbClient);
    }

    public List<String> listTableNames() {
        return client.listTablesPaginator().stream()
                .map(ListTablesResponse::tableNames)
                .flatMap(List::stream)
                .toList();
    }

    public Optional<TableDescription> getTableDescription(final String tableName) {
        try {
            final var request =
                    DescribeTableRequest.builder().tableName(tableName).build();
            final var tableDescription = client.describeTable(request).table();
            return Optional.of(tableDescription);
        } catch (final ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }

    public Optional<ContinuousBackupsDescription> getContinuousBackupsDescription(final String tableName) {
        try {
            final var request = DescribeContinuousBackupsRequest.builder()
                    .tableName(tableName)
                    .build();
            final var continuousBackupsDescription =
                    client.describeContinuousBackups(request).continuousBackupsDescription();
            return Optional.of(continuousBackupsDescription);
        } catch (final ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }
}
