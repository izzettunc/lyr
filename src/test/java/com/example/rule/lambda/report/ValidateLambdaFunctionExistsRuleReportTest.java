package com.example.rule.lambda.report;

import static com.example.TestUtil.FUNCTION_1;
import static com.example.TestUtil.FUNCTION_2;
import static com.example.TestUtil.FUNCTION_3;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.SsmReason;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateLambdaFunctionExistsRuleReportTest {

    ValidateLambdaFunctionExistsRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateLambdaFunctionExistsRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3))
                .build();

        final var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND));

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(FUNCTION_1, FUNCTION_2, FUNCTION_3)
                .contains("null", "FUNCTION_NOT_FOUND")
                .contains(VALIDATE_LAMBDA_FUNCTION_EXISTS);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of())
                .build();

        final List<ValidationOutcome<SsmReason>> outcome = ImmutableList.of();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_LAMBDA_FUNCTION_EXISTS);
    }
}
