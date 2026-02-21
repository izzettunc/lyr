package com.example.services.dynamodb;

import com.example.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

public class DynamoDbConnector {

    private final DynamoDbClient client;

    private DynamoDbConnector(DynamoDbClient client) {
        this.client = client;
    }

    public static DynamoDbConnector create() {
        return new DynamoDbConnector(ServiceProvider.getOrBuildDynamoDbClient());
    }

    public static DynamoDbConnector create(DynamoDbClient client) {
        return new DynamoDbConnector(client);
    }

    public List<ListTablesResponse> listTables() {
        return client.listTablesPaginator().stream().toList();
    }

    public Optional<DescribeTableResponse> getTable(String tableName) {
        try {
            return Optional.of(client.describeTable(
                    DescribeTableRequest.builder().tableName(tableName).build()));
        } catch (ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }
}
