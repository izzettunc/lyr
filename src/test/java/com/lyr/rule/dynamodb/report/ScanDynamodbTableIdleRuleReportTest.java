package com.lyr.rule.dynamodb.report;

import static com.lyr.TestUtil.createImmutableListOfScanOutcome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.TestUtil;
import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanDynamodbTableIdleRuleReportTest {

    static final MockedStatic<ConsoleReportStyler> mockedConsoleReportStyler =
            mockStatic(ConsoleReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ScanDynamodbTableIdleRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanDynamodbTableIdleRuleReport();
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
                ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(1).build();

        final var outcome = createImmutableListOfScanOutcome(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);
        final var expectedRawLines = List.of(
                "DynamoDB table 'dummy' has been idle for more than 1 days.",
                "DynamoDB table 'dummy2' has been idle for more than 1 days.");

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
                ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(1).build();

        final var outcome = createImmutableListOfScanOutcome();
        final var expectedRawLines = List.of("No idle DynamoDB table found that are idle longer than 1 days.");

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
