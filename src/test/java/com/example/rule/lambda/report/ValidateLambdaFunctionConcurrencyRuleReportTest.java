package com.example.rule.lambda.report;

import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateLambdaFunctionConcurrencyRuleReportTest {

    ValidateLambdaFunctionConcurrencyRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateLambdaFunctionConcurrencyRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function1", 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function2", 2),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function3", 3),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("function4", 4)))
                .build();

        var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_CONCURRENCY_MISMATCH));

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains("function1", "function2", "function3", "function4")
                .contains("null", "FUNCTION_NOT_FOUND", "FUNCTION_CONCURRENCY_MISMATCH")
                .contains(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of())
                .build();

        ImmutableList<ValidationOutcome<LambdaReason>> outcome = ImmutableList.of();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY);
    }
}
