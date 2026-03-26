package com.lyr.services.lambda;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
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

    public List<EventSourceMappingConfiguration> listEventSourceMappings(final String functionName) {
        return client.listEventSourceMappingsPaginator(builder -> builder.functionName(functionName)).stream()
                .map(ListEventSourceMappingsResponse::eventSourceMappings)
                .flatMap(List::stream)
                .toList();
    }

    public List<String> listLambdaFunctionNames() {
        return client.listFunctionsPaginator().stream()
                .map(ListFunctionsResponse::functions)
                .flatMap(List::stream)
                .map(FunctionConfiguration::functionName)
                .toList();
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
