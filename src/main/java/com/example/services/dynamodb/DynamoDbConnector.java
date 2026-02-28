package com.example.services.dynamodb;

import com.example.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

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

    public Optional<DescribeTableResponse> getTable(final String tableName) {
        try {
            return Optional.of(client.describeTable(
                    DescribeTableRequest.builder().tableName(tableName).build()));
        } catch (final ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }
}
