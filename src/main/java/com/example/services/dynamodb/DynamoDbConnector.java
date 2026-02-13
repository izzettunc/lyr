package com.example.services.dynamodb;

import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public class DynamoDbConnector {

    private static DynamoDbClient client;
    private static DynamoDbConnector instance;

    private DynamoDbConnector() {
        client = buildClient();
    }

    public static DynamoDbConnector getInstance() {
        if (null == instance) {
            instance = new DynamoDbConnector();
        }

        return instance;
    }

    public List<ListTablesResponse> listTables() {
        return client.listTablesPaginator().stream().toList();
    }

    public Optional<DescribeTableResponse> getTable(String tableName) {
        try {
            return Optional.of(client.describeTable(DescribeTableRequest
                    .builder()
                    .tableName(tableName)
                    .build()));
        } catch (ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }

    private static DynamoDbClient buildClient() {
        if (!StringUtils.isBlank(System.getenv(SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable()))) {
            return DynamoDbClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return DynamoDbClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }
}
