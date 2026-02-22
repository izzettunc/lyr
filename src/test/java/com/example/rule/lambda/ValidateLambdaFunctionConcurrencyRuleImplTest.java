package com.example.rule.lambda;

import static com.example.TestUtil.FUNCTION_1;
import static com.example.TestUtil.FUNCTION_2;
import static com.example.TestUtil.FUNCTION_3;
import static com.example.TestUtil.FUNCTION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.Concurrency;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;

class ValidateLambdaFunctionConcurrencyRuleImplTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ValidateLambdaFunctionConcurrencyRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
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
        final var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_1, 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_2, 1)))
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder()
                .concurrency(
                        Concurrency.builder().reservedConcurrentExecutions(1).build())
                .build());

        final var expectedResult = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.valid());

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleReturnsInvalidWhenFunctionNotFoundWithCorrectReason() {
        // Given
        final var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_1, 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_2, 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_3, 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_4, 1)))
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder()
                .concurrency(
                        Concurrency.builder().reservedConcurrentExecutions(1).build())
                .build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.valid());

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq(FUNCTION_1), eq(FUNCTION_4))))
                .thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq(FUNCTION_2), eq(FUNCTION_3))))
                .thenReturn(Optional.empty());

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleReturnsInvalidWhenConcurrencyIsNotExpectedWithCorrectReason() {
        // Given
        final var expectedConcurrency = 1;
        final var invalidConcurrency = 999;
        final var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(
                                FUNCTION_1, expectedConcurrency),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(
                                FUNCTION_2, expectedConcurrency),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(
                                FUNCTION_3, expectedConcurrency),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(
                                FUNCTION_4, expectedConcurrency)))
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder()
                .concurrency(Concurrency.builder()
                        .reservedConcurrentExecutions(expectedConcurrency)
                        .build())
                .build());

        final var optLambdaFunctionDifferentCur = Optional.of(GetFunctionResponse.builder()
                .concurrency(Concurrency.builder()
                        .reservedConcurrentExecutions(invalidConcurrency)
                        .build())
                .build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_CONCURRENCY_MISMATCH),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_CONCURRENCY_MISMATCH),
                ValidationOutcome.valid());

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq(FUNCTION_1), eq(FUNCTION_4))))
                .thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq(FUNCTION_2), eq(FUNCTION_3))))
                .thenReturn(optLambdaFunctionDifferentCur);

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}
