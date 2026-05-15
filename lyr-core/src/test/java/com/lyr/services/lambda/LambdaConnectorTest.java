package com.lyr.services.lambda;

import static com.lyr.TestUtil.DUMMY2_STRING;
import static com.lyr.TestUtil.DUMMY_STRING;
import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.lyr.services.ServiceProvider;
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
import software.amazon.awssdk.services.lambda.paginators.ListEventSourceMappingsIterable;
import software.amazon.awssdk.services.lambda.paginators.ListFunctionsIterable;

class LambdaConnectorTest {

    final LambdaClient mockedLambdaClient = mock(LambdaClient.class);
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
        try (final var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            LambdaConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildLambdaClient, times(1));
        }
    }

    @Test
    void testThatLambdaConnectorListLambdaFunctionsReturnsListOfFunctionNames() {
        // Given
        final var listOfLisFunctionsResponse = List.of(
                ListFunctionsResponse.builder()
                        .functions(
                                FunctionConfiguration.builder()
                                        .functionName(FUNCTION_1)
                                        .build(),
                                FunctionConfiguration.builder()
                                        .functionName(FUNCTION_2)
                                        .build())
                        .build(),
                ListFunctionsResponse.builder()
                        .functions(
                                FunctionConfiguration.builder()
                                        .functionName(FUNCTION_3)
                                        .build(),
                                FunctionConfiguration.builder()
                                        .functionName(FUNCTION_4)
                                        .build())
                        .build());

        final var mockedListFunctionsIterable = mock(ListFunctionsIterable.class);

        final var expectedListOfFunctionNames = List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4);

        // When
        when(mockedListFunctionsIterable.stream()).thenReturn(listOfLisFunctionsResponse.stream());
        when(mockedLambdaClient.listFunctionsPaginator()).thenReturn(mockedListFunctionsIterable);

        final var actualResult = testObject.listLambdaFunctionNames();

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedListOfFunctionNames);
    }

    @Test
    void testThatLambdaConnectorListEventSourceMappingsReturnsListOfEventSourceMappings() {
        // Given
        final var listEventSourceMappingsResponse = List.of(ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder()
                                .eventSourceArn(DUMMY_STRING)
                                .build(),
                        EventSourceMappingConfiguration.builder()
                                .eventSourceArn(DUMMY2_STRING)
                                .build())
                .build());

        final var expectedListOfEventSourceMappings = List.of(
                EventSourceMappingConfiguration.builder()
                        .eventSourceArn(DUMMY_STRING)
                        .build(),
                EventSourceMappingConfiguration.builder()
                        .eventSourceArn(DUMMY2_STRING)
                        .build());

        final var mockedListEventSourceMappingsIterable = mock(ListEventSourceMappingsIterable.class);

        // When
        when(mockedListEventSourceMappingsIterable.stream()).thenReturn(listEventSourceMappingsResponse.stream());
        when(mockedLambdaClient.listEventSourceMappingsPaginator(any(Consumer.class)))
                .thenReturn(mockedListEventSourceMappingsIterable);

        final var actualResult = testObject.listEventSourceMappings(DUMMY_STRING);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedListOfEventSourceMappings);
    }

    @Test
    void testThatLambdaConnectorListEventSourceMappingsThrowsResourceNotFoundExceptionWhenResourceNotFound() {
        // Given
        // When
        when(mockedLambdaClient.listEventSourceMappingsPaginator(any(Consumer.class)))
                .thenThrow(ResourceNotFoundException.class);

        assertThatThrownBy(() -> testObject.listEventSourceMappings(DUMMY_STRING))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testThatLambdaConnectorGetLambdaFunctionReturnsOptionalGetFunctionResponse() {
        // Given
        final var getFunctionResponse = GetFunctionResponse.builder()
                .configuration(
                        FunctionConfiguration.builder().functionName("lambda1").build())
                .build();

        // When
        when(mockedLambdaClient.getFunction(any(GetFunctionRequest.class))).thenReturn(getFunctionResponse);

        final var actualResult = testObject.getLambdaFunction(DUMMY_STRING);

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

        final var actualResult = testObject.getLambdaFunction(DUMMY_STRING);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Optional.empty());
    }
}
