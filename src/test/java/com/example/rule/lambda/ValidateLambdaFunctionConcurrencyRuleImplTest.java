package com.example.rule.lambda;

import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.Concurrency;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class ValidateLambdaFunctionConcurrencyRuleImplTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ValidateLambdaFunctionConcurrencyRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::getInstance).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ValidateLambdaFunctionConcurrencyRuleImpl();
    }

    @AfterEach
    public void afterEach() {
        mockedLambdaConnector.reset();
        reset(mockedLambdaConnectorInstance);
    }

    @AfterAll
    public static void afterAll() {
        mockedLambdaConnector.closeOnDemand();
    }

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleExecutesSuccessfully() {
        // Given
        var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function1", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function2", 1)
                ))
                .build();

        var optLambdaFunction = Optional.of(
                GetFunctionResponse.builder()
                        .concurrency(
                                Concurrency.builder()
                                        .reservedConcurrentExecutions(1)
                                        .build())
                        .build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.valid()
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleReturnsInvalidWhenFunctionNotFoundWithCorrectReason() {
        // Given
        var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function1", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function2", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function3", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function4", 1)
                ))
                .build();

        var optLambdaFunction = Optional.of(
                GetFunctionResponse.builder()
                        .concurrency(
                                Concurrency.builder()
                                        .reservedConcurrentExecutions(1)
                                        .build())
                        .build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.valid()
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq("function1"), eq("function4")))).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq("function2"), eq("function3")))).thenReturn(Optional.empty());

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleReturnsInvalidWhenConcurrencyIsNotExpectedWithCorrectReason() {
        // Given
        var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function1", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function2", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function3", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function4", 1)
                ))
                .build();

        var optLambdaFunction = Optional.of(
                GetFunctionResponse.builder()
                        .concurrency(
                                Concurrency.builder()
                                        .reservedConcurrentExecutions(1)
                                        .build())
                        .build());

        var optLambdaFunctionDifferentCur = Optional.of(
                GetFunctionResponse.builder()
                        .concurrency(
                                Concurrency.builder()
                                        .reservedConcurrentExecutions(999)
                                        .build())
                        .build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_CONCURRENCY_MISMATCH),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_CONCURRENCY_MISMATCH),
                ValidationOutcome.valid()
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq("function1"), eq("function4")))).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq("function2"), eq("function3")))).thenReturn(optLambdaFunctionDifferentCur);

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}