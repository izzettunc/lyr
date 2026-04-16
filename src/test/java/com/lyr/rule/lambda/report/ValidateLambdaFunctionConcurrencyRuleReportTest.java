package com.lyr.rule.lambda.report;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.google.common.collect.ImmutableList;
import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.lyr.rule.outcome.ValidationOutcome;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ValidateLambdaFunctionConcurrencyRuleReportTest {

    static final MockedStatic<ConsoleReportStyler> mockedConsoleReportStyler =
            mockStatic(ConsoleReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ValidateLambdaFunctionConcurrencyRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateLambdaFunctionConcurrencyRuleReport();
        mockedConsoleReportStyler.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedConsoleReportStyler.closeOnDemand();
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
        final var expectedRawLines = List.of(
                "Lambda function 'function1' has expected concurrency.",
                "Lambda function 'function2' validation failed: null",
                "Lambda function 'function3' validation failed: FUNCTION_NOT_FOUND",
                "Lambda function 'function4' validation failed: FUNCTION_CONCURRENCY_MISMATCH");

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
