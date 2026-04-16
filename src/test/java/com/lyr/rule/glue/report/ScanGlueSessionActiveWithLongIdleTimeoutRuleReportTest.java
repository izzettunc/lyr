package com.lyr.rule.glue.report;

import static com.lyr.TestUtil.createImmutableListOfScanOutcome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.TestUtil;
import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleReportTest {

    static final MockedStatic<ConsoleReportStyler> mockedConsoleReportStyler =
            mockStatic(ConsoleReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ScanGlueSessionActiveWithLongIdleTimeoutRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanGlueSessionActiveWithLongIdleTimeoutRuleReport();
        mockedConsoleReportStyler.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedConsoleReportStyler.closeOnDemand();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(1)
                .build();

        final var outcome = createImmutableListOfScanOutcome(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);
        final var expectedRawLines = List.of(
                "Session 'dummy' has been active for more than idle timeout of 1 minutes.",
                "Session 'dummy2' has been active for more than idle timeout of 1 minutes.");

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
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(1)
                .build();

        final var outcome = createImmutableListOfScanOutcome();
        final var expectedRawLines = List.of("No active sessions found with idle timeout greater than 1 minutes.");

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
