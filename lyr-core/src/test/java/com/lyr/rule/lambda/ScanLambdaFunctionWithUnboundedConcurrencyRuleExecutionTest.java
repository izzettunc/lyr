package com.lyr.rule.lambda;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.Concurrency;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleExecutionTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution();
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
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        final var listOfFunctionNames = List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4);

        final var expectedResult = Stream.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionNames()).thenReturn(listOfFunctionNames);
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_1)).thenReturn(createOptFunction(FUNCTION_1));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_2)).thenReturn(createOptFunction(FUNCTION_2));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_3)).thenReturn(createOptFunction(FUNCTION_3));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_4)).thenReturn(createOptFunction(FUNCTION_4));
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleReturnsOnlyTheFunctionsWithUnboundedConcurrency() {
        // Given
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        final var listOfFunctionNames = List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4);

        final var expectedResult =
                Stream.of(FUNCTION_2, FUNCTION_3).map(Finding::byId).collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionNames()).thenReturn(listOfFunctionNames);
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_1))
                .thenReturn(createOptFunction(FUNCTION_1, false));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_2)).thenReturn(createOptFunction(FUNCTION_2));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_3)).thenReturn(createOptFunction(FUNCTION_3));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_4))
                .thenReturn(createOptFunction(FUNCTION_4, false));
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleWorksOnFunctionsThatExists() {
        // Given
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        final var listOfFunctionNames = List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4);

        final var expectedResult =
                Stream.of(FUNCTION_2, FUNCTION_3).map(Finding::byId).collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionNames()).thenReturn(listOfFunctionNames);
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_1)).thenReturn(Optional.empty());
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_2)).thenReturn(createOptFunction(FUNCTION_2));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_3)).thenReturn(createOptFunction(FUNCTION_3));
        when(mockedLambdaConnectorInstance.getLambdaFunction(FUNCTION_4)).thenReturn(Optional.empty());
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleReturnsEmptyListWhenNoFunctionHasBeenFound() {
        // Given
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        final List<String> listOfFunctionNames = List.of();

        final var expectedResult = ImmutableList.of();

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionNames()).thenReturn(listOfFunctionNames);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    private static Optional<GetFunctionResponse> createOptFunction(final String functionName, final boolean unbounded) {
        Concurrency concurrency = null;
        if (!unbounded) {
            concurrency = Concurrency.builder().reservedConcurrentExecutions(1).build();
        }

        return Optional.of(GetFunctionResponse.builder()
                .concurrency(concurrency)
                .configuration(FunctionConfiguration.builder()
                        .functionName(functionName)
                        .build())
                .build());
    }

    private static Optional<GetFunctionResponse> createOptFunction(final String functionName) {
        return createOptFunction(functionName, true);
    }
}
