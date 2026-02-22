package com.example.services.lambda;

import com.example.services.ServiceProvider;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ListEventSourceMappingsResponse;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;

public final class LambdaConnector {
    private final LambdaClient client;

    private LambdaConnector(final LambdaClient lambdaClient) {
        this.client = lambdaClient;
    }

    public static LambdaConnector create() {
        return new LambdaConnector(ServiceProvider.getOrBuildLambdaClient());
    }

    @VisibleForTesting
    static LambdaConnector create(final LambdaClient lambdaClient) {
        return new LambdaConnector(lambdaClient);
    }

    public Optional<ListEventSourceMappingsResponse> listEventSourceMappings(final String functionName) {
        try {
            return Optional.of(client.listEventSourceMappings(builder -> builder.functionName(functionName)));
        } catch (final ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }

    public List<ListFunctionsResponse> listLambdaFunctions() {
        return client.listFunctionsPaginator().stream().toList();
    }

    public Optional<GetFunctionResponse> getLambdaFunction(final String functionName) {
        try {
            return Optional.of(client.getFunction(
                    GetFunctionRequest.builder().functionName(functionName).build()));
        } catch (final ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }
}
