package com.example.services.lambda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.example.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ListEventSourceMappingsResponse;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;
import software.amazon.awssdk.services.lambda.paginators.ListFunctionsIterable;

class LambdaConnectorTest {

    LambdaClient mockedLambdaClient = mock(LambdaClient.class);
    LambdaConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = LambdaConnector.create(mockedLambdaClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedLambdaClient);
    }

    @Test
    void testThatLambdaConnectorGetsLambdaClientFromServiceProvider() {
        // Given
        try (var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            LambdaConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildLambdaClient, times(1));
        }
    }

    @Test
    void testThatLambdaConnectorListLambdaFunctionsReturnsListOfListFunctionsResponse() {
        // Given
        var listOfLisFunctionsResponse = List.of(
                ListFunctionsResponse.builder()
                        .functions(
                                FunctionConfiguration.builder()
                                        .functionName("function1")
                                        .build(),
                                FunctionConfiguration.builder()
                                        .functionName("function2")
                                        .build())
                        .build(),
                ListFunctionsResponse.builder()
                        .functions(
                                FunctionConfiguration.builder()
                                        .functionName("function3")
                                        .build(),
                                FunctionConfiguration.builder()
                                        .functionName("function4")
                                        .build())
                        .build());
        var mockedListFunctionsIterable = mock(ListFunctionsIterable.class);

        // When
        when(mockedListFunctionsIterable.stream()).thenReturn(listOfLisFunctionsResponse.stream());
        when(mockedLambdaClient.listFunctionsPaginator()).thenReturn(mockedListFunctionsIterable);

        var actualResult = testObject.listLambdaFunctions();

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(listOfLisFunctionsResponse);
    }

    @Test
    void testThatLambdaConnectorListEventSourceMappingsReturnsOptionalListEventSourceMappingResponse() {
        // Given
        var listEventSourceMappingsResponse = ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder()
                                .eventSourceArn("map1")
                                .build(),
                        EventSourceMappingConfiguration.builder()
                                .eventSourceArn("map2")
                                .build())
                .build();

        // When
        when(mockedLambdaClient.listEventSourceMappings(any(Consumer.class)))
                .thenReturn(listEventSourceMappingsResponse);

        var actualResult = testObject.listEventSourceMappings("dummy");

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Optional.of(listEventSourceMappingsResponse));
    }

    @Test
    void testThatLambdaConnectorListEventSourceMappingsReturnsEmptyOptionalWhenFunctionDoesntExist() {
        // Given
        // When
        when(mockedLambdaClient.listEventSourceMappings(any(Consumer.class)))
                .thenThrow(ResourceNotFoundException.class);

        var actualResult = testObject.listEventSourceMappings("dummy");

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Optional.empty());
    }

    @Test
    void testThatLambdaConnectorGetLambdaFunctionReturnsOptionalGetFunctionResponse() {
        // Given
        var getFunctionResponse = GetFunctionResponse.builder()
                .configuration(
                        FunctionConfiguration.builder().functionName("lambda1").build())
                .build();

        // When
        when(mockedLambdaClient.getFunction(any(GetFunctionRequest.class))).thenReturn(getFunctionResponse);

        var actualResult = testObject.getLambdaFunction("dummy");

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Optional.of(getFunctionResponse));
    }

    @Test
    void testThatLambdaConnectorGetLambdaFunctionReturnsEmptyOptionalWhenFunctionDoesntExist() {
        // Given
        // When
        when(mockedLambdaClient.getFunction(any(GetFunctionRequest.class))).thenThrow(ResourceNotFoundException.class);

        var actualResult = testObject.getLambdaFunction("dummy");

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Optional.empty());
    }
}
