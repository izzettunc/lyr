package com.example.rule.lambda.report;

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
        var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of("function1", "function2", "function3"))
                .build();

        var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND));

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains("function1", "function2", "function3")
                .contains("null", "FUNCTION_NOT_FOUND")
                .contains(VALIDATE_LAMBDA_FUNCTION_EXISTS);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of())
                .build();

        ImmutableList<ValidationOutcome<SsmReason>> outcome = ImmutableList.of();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_LAMBDA_FUNCTION_EXISTS);
    }
}
