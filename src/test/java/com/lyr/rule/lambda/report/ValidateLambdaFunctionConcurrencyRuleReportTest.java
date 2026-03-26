package com.lyr.rule.lambda.report;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.lyr.rule.outcome.ValidationOutcome;
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
        final var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of(
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_1, 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_2, 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_3, 1),
                        new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_4, 1)))
                .build();

        final var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_CONCURRENCY_MISMATCH));

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4)
                .contains("null", "FUNCTION_NOT_FOUND", "FUNCTION_CONCURRENCY_MISMATCH")
                .contains(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(List.of())
                .build();

        final List<ValidationOutcome<LambdaReason>> outcome = ImmutableList.of();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY);
    }
}
