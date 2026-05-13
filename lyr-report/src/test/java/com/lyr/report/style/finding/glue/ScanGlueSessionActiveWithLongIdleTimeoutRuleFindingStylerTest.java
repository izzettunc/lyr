package com.lyr.report.style.finding.glue;

import static com.lyr.report.TestUtil.createImmutableListOfFindings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.report.TestUtil;
import com.lyr.report.plain.text.Sentiment;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStylerTest {

    static final MockedStatic<PlainTextReportStyler> mockedPlainTextReportStyler =
            mockStatic(PlainTextReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler();
        mockedPlainTextReportStyler.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedPlainTextReportStyler.closeOnDemand();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereAreFindings() {
        // Given
        final var findings = createImmutableListOfFindings(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);
        final var expectedRawLines = List.of(
                "Session 'dummy' has been active for more than max idle timeout.",
                "Session 'dummy2' has been active for more than max idle timeout.");

        // When
        final var actualResult = testObject.styleForPlainText(findings);

        // Then
        assertThat(actualResult).containsSubsequence(expectedRawLines);
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.styleFindingReport(anyString(), any(Sentiment.class)),
                times(expectedRawLines.size()));
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.toNewLine(anyString()), times(expectedRawLines.size()));
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereAreNoFindings() {
        // Given
        final var findings = createImmutableListOfFindings();
        final var expectedRawLines = List.of("No active sessions found with idle timeout more than max idle timeout.");

        // When
        final var actualResult = testObject.styleForPlainText(findings);

        // Then
        assertThat(actualResult).containsSubsequence(expectedRawLines);
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.styleFindingReport(anyString(), any(Sentiment.class)),
                times(expectedRawLines.size()));
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.toNewLine(anyString()), times(expectedRawLines.size()));
    }
}
