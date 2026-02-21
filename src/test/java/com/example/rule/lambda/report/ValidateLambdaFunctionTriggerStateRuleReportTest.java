package com.example.rule.lambda.report;

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
        var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(List.of(
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function1", true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function2", false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function3", false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function4", true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function5", true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function6", false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function7", true),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("function8", false),
                        new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(
                                "function9", false)))
                .build();

        var outcome = ImmutableList.of(
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
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(
                        "function1",
                        "function2",
                        "function3",
                        "function4",
                        "function5",
                        "function6",
                        "function7",
                        "function8",
                        "function9")
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
        var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(List.of())
                .build();

        ImmutableList<ValidationOutcome<LambdaReason>> outcome = ImmutableList.of();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE);
    }
}
