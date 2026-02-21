package com.example.rule.lambda;


import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.example.rule.outcome.ScanOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.Concurrency;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;


import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleImplTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl();
    }

    @AfterEach
    void afterEach() {
        mockedLambdaConnector.reset();
        reset(mockedLambdaConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedLambdaConnector.closeOnDemand();
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleExecutesSuccessfully() {
        // Given
        var config = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        var listOfListFunctionsResponse = List.of(
                ListFunctionsResponse.builder().functions(
                        FunctionConfiguration.builder().functionName("function1").build(),
                        FunctionConfiguration.builder().functionName("function2").build()
                ).build(),
                ListFunctionsResponse.builder().functions(
                        FunctionConfiguration.builder().functionName("function3").build(),
                        FunctionConfiguration.builder().functionName("function4").build()
                ).build()
        );


        var expectedResult = Stream.of("function1", "function2", "function3", "function4")
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctions()).thenReturn(listOfListFunctionsResponse);
        when(mockedLambdaConnectorInstance.getLambdaFunction("function1")).thenReturn(createOptFunction("function1"));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function2")).thenReturn(createOptFunction("function2"));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function3")).thenReturn(createOptFunction("function3"));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function4")).thenReturn(createOptFunction("function4"));
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleReturnsOnlyTheFunctionsWithUnboundedConcurrency() {
        // Given
        var config = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        var listOfListFunctionsResponse = List.of(
                ListFunctionsResponse.builder().functions(
                        FunctionConfiguration.builder().functionName("function1").build(),
                        FunctionConfiguration.builder().functionName("function2").build()
                ).build(),
                ListFunctionsResponse.builder().functions(
                        FunctionConfiguration.builder().functionName("function3").build(),
                        FunctionConfiguration.builder().functionName("function4").build()
                ).build()
        );


        var expectedResult = Stream.of("function2", "function3")
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctions()).thenReturn(listOfListFunctionsResponse);
        when(mockedLambdaConnectorInstance.getLambdaFunction("function1")).thenReturn(createOptFunction("function1", false));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function2")).thenReturn(createOptFunction("function2"));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function3")).thenReturn(createOptFunction("function3"));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function4")).thenReturn(createOptFunction("function4", false));
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleWorksOnFunctionsThatExists() {
        // Given
        var config = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        var listOfListFunctionsResponse = List.of(
                ListFunctionsResponse.builder().functions(
                        FunctionConfiguration.builder().functionName("function1").build(),
                        FunctionConfiguration.builder().functionName("function2").build()
                ).build(),
                ListFunctionsResponse.builder().functions(
                        FunctionConfiguration.builder().functionName("function3").build(),
                        FunctionConfiguration.builder().functionName("function4").build()
                ).build()
        );


        var expectedResult = Stream.of("function2", "function3")
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctions()).thenReturn(listOfListFunctionsResponse);
        when(mockedLambdaConnectorInstance.getLambdaFunction("function1")).thenReturn(Optional.empty());
        when(mockedLambdaConnectorInstance.getLambdaFunction("function2")).thenReturn(createOptFunction("function2"));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function3")).thenReturn(createOptFunction("function3"));
        when(mockedLambdaConnectorInstance.getLambdaFunction("function4")).thenReturn(Optional.empty());
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }


    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleReturnsEmptyListWhenNoFunctionHasBeenFound() {
        // Given
        var config = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        var listOfListFunctionsResponse = List.of(ListFunctionsResponse.builder().build());

        var expectedResult = ImmutableList.of();

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctions()).thenReturn(listOfListFunctionsResponse);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    private static Optional<GetFunctionResponse> createOptFunction(String functionName, boolean unbounded) {
        Concurrency concurrency = null;
        if (!unbounded) {
            concurrency = Concurrency.builder().reservedConcurrentExecutions(5).build();
        }

        return Optional.of(
                GetFunctionResponse.builder()
                        .concurrency(concurrency)
                        .configuration(
                                FunctionConfiguration.builder()
                                        .functionName(functionName)
                                        .build())
                        .build());
    }

    private static Optional<GetFunctionResponse> createOptFunction(String functionName) {
        return createOptFunction(functionName, true);
    }
}