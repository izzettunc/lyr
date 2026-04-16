package com.lyr.rule.lambda.report;

import static com.lyr.TestUtil.createImmutableListOfScanOutcome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.TestUtil;
import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleReportTest {

    static final MockedStatic<ConsoleReportStyler> mockedConsoleReportStyler =
            mockStatic(ConsoleReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ScanLambdaFunctionWithUnboundedConcurrencyRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanLambdaFunctionWithUnboundedConcurrencyRuleReport();
        mockedConsoleReportStyler.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedConsoleReportStyler.closeOnDemand();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        final var outcome = createImmutableListOfScanOutcome(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);
        final var expectedRawLines = List.of(
                "Lambda function 'dummy' has unbounded concurrency.",
                "Lambda function 'dummy2' has unbounded concurrency.");

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

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        final var outcome = createImmutableListOfScanOutcome();
        final var expectedRawLines = List.of("No lambda found with unbounded concurrency.");

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
