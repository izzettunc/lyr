package com.lyr.rule.lambda.report;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static com.lyr.TestUtil.FUNCTION_5;
import static com.lyr.TestUtil.FUNCTION_6;
import static com.lyr.TestUtil.FUNCTION_7;
import static com.lyr.TestUtil.FUNCTION_8;
import static com.lyr.TestUtil.FUNCTION_9;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.google.common.collect.ImmutableList;
import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.lyr.rule.outcome.ValidationOutcome;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ValidateLambdaFunctionTriggerStateRuleReportTest {

    static final MockedStatic<ConsoleReportStyler> mockedConsoleReportStyler =
            mockStatic(ConsoleReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ValidateLambdaFunctionTriggerStateRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateLambdaFunctionTriggerStateRuleReport();
        mockedConsoleReportStyler.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedConsoleReportStyler.closeOnDemand();
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

        final var expectedRawLines = List.of(
                "Lambda function 'function1' expected to be enabled. Validation passed: ALL_EVENT_MAPPINGS_ARE_ENABLED",
                "Lambda function 'function2' expected to be disabled. Validation failed: ALL_EVENT_MAPPINGS_ARE_ENABLED",
                "Lambda function 'function3' expected to be disabled. Validation passed: ALL_EVENT_MAPPINGS_ARE_DISABLED",
                "Lambda function 'function4' expected to be enabled. Validation failed: ALL_EVENT_MAPPINGS_ARE_DISABLED",
                "Lambda function 'function5' expected to be enabled. Validation failed: ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED",
                "Lambda function 'function6' expected to be disabled. Validation failed: ALL_EVENT_MAPPINGS_ARE_NOT_DISABLED",
                "Lambda function 'function7' expected to be enabled. Validation failed: NO_EVENT_SOURCE_MAPPINGS",
                "Lambda function 'function8' expected to be disabled. Validation passed: NO_EVENT_SOURCE_MAPPINGS",
                "Lambda function 'function9' expected to be disabled. Validation failed: FUNCTION_NOT_FOUND");

        // When
        final var actualResult = testObject.reportToConsole(config, outcome);

        // Then
        assertThat(actualResult).containsSubsequence(expectedRawLines);
        mockedConsoleReportStyler.verify(
                () -> ConsoleReportStyler.styleOutcome(anyString(), any(Sentiment.class)),
                times(expectedRawLines.size()));
        mockedConsoleReportStyler.verify(
                () -> ConsoleReportStyler.toNewLine(anyString()), times(expectedRawLines.size()));
    }
}
