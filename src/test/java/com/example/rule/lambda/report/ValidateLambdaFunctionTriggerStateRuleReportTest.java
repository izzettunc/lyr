package com.example.rule.lambda.report;

import static com.example.TestUtil.FUNCTION_1;
import static com.example.TestUtil.FUNCTION_2;
import static com.example.TestUtil.FUNCTION_3;
import static com.example.TestUtil.FUNCTION_4;
import static com.example.TestUtil.FUNCTION_5;
import static com.example.TestUtil.FUNCTION_6;
import static com.example.TestUtil.FUNCTION_7;
import static com.example.TestUtil.FUNCTION_8;
import static com.example.TestUtil.FUNCTION_9;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateLambdaFunctionTriggerStateRuleReportTest {

    ValidateLambdaFunctionTriggerStateRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateLambdaFunctionTriggerStateRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(List.of(
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_1, true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_2, false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_3, false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_4, true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_5, true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_6, false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_7, true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_8, false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_9, false)))
                .build();

        final var outcome = ImmutableList.of(
                ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED),
                ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_DISABLED),
                ValidationOutcome.invalid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS),
                ValidationOutcome.valid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND));

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(
                        FUNCTION_1,
                        FUNCTION_2,
                        FUNCTION_3,
                        FUNCTION_4,
                        FUNCTION_5,
                        FUNCTION_6,
                        FUNCTION_7,
                        FUNCTION_8,
                        FUNCTION_9)
                .contains("enabled", "disabled")
                .contains(
                        "ALL_EVENT_MAPPINGS_ARE_ENABLED",
                        "ALL_EVENT_MAPPINGS_ARE_DISABLED",
                        "ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED",
                        "ALL_EVENT_MAPPINGS_ARE_NOT_DISABLED",
                        "NO_EVENT_SOURCE_MAPPINGS",
                        "FUNCTION_NOT_FOUND")
                .contains(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(List.of())
                .build();

        final ImmutableList<ValidationOutcome<LambdaReason>> outcome = ImmutableList.of();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE);
    }
}
