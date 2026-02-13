package com.example.services.lambda;

import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ListEventSourceMappingsResponse;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public class LambdaConnector {
    private static LambdaClient client;
    private static LambdaConnector instance;

    private LambdaConnector() {
        client = buildClient();
    }

    public static LambdaConnector getInstance() {
        if (instance == null) {
            instance = new LambdaConnector();
        }

        return instance;
    }

    public Optional<ListEventSourceMappingsResponse> listEventSourceMappings(String functionName) {
        try {
            return Optional.of(client.listEventSourceMappings(builder -> builder.functionName(functionName)));
        } catch (ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }

    public List<ListFunctionsResponse> listLambdaFunctions() {
        return client.listFunctionsPaginator().stream().toList();
    }

    public Optional<GetFunctionResponse> getLambdaFunction(String functionName) {
        try {
            return Optional.of(client.getFunction(GetFunctionRequest.builder().functionName(functionName).build()));
        } catch (ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }

    private static LambdaClient buildClient() {
        if (!StringUtils.isBlank(System.getenv(SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable()))) {
            return LambdaClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return LambdaClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }
}
