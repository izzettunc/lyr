package com.lyr.report.style.dynamodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import com.lyr.report.TestUtil;
import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanDynamodbTableIdleRuleFindingStylerTest {

    static final MockedStatic<ConsoleReportStyler> mockedConsoleReportStyler =
            mockStatic(ConsoleReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ScanDynamodbTableIdleRuleFindingStyler testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanDynamodbTableIdleRuleFindingStyler();
        mockedConsoleReportStyler.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedConsoleReportStyler.closeOnDemand();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereAreFindings() {
        // Given
        final var findings = TestUtil.createImmutableListOfFindings(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);
        final var expectedRawLines = List.of(
                "DynamoDB table 'dummy' has been idle longer than max idle period.",
                "DynamoDB table 'dummy2' has been idle longer than max idle period.");

        // When
        final var actualResult = testObject.styleForConsole(findings);

        // Then
        assertThat(actualResult).containsSubsequence(expectedRawLines);
        mockedConsoleReportStyler.verify(
                () -> ConsoleReportStyler.styleFindingReport(anyString(), any(Sentiment.class)),
                Mockito.times(expectedRawLines.size()));
        mockedConsoleReportStyler.verify(
                () -> ConsoleReportStyler.toNewLine(anyString()), Mockito.times(expectedRawLines.size()));
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereAreNoFindings() {
        // Given
        final var findings = TestUtil.createImmutableListOfFindings();
        final var expectedRawLines =
                List.of("No idle DynamoDB table found that has been idle longer than max idle period.");

        // When
        final var actualResult = testObject.styleForConsole(findings);

        // Then
        assertThat(actualResult).containsSubsequence(expectedRawLines);
        mockedConsoleReportStyler.verify(
                () -> ConsoleReportStyler.styleFindingReport(anyString(), any(Sentiment.class)),
                Mockito.times(expectedRawLines.size()));
        mockedConsoleReportStyler.verify(
                () -> ConsoleReportStyler.toNewLine(anyString()), Mockito.times(expectedRawLines.size()));
    }
}
