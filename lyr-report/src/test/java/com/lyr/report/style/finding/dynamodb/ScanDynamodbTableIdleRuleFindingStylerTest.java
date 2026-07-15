package com.lyr.report.style.finding.dynamodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import com.lyr.report.TestUtil;
import com.lyr.report.plain.text.Sentiment;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanDynamodbTableIdleRuleFindingStylerTest {

    static final MockedStatic<PlainTextReportStyler> mockedPlainTextReportStyler =
            mockStatic(PlainTextReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ScanDynamodbTableIdleRuleFindingStyler testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanDynamodbTableIdleRuleFindingStyler();
        mockedPlainTextReportStyler.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedPlainTextReportStyler.closeOnDemand();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereAreFindings() {
        // Given
        final var findings = TestUtil.createImmutableListOfFindings(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);
        final var expectedRawLines = List.of(
                "DynamoDB table 'dummy' has been idle longer than max idle period.",
                "DynamoDB table 'dummy2' has been idle longer than max idle period.");

        // When
        final var actualResult = testObject.styleForPlainText(findings);

        // Then
        assertThat(actualResult).containsSubsequence(expectedRawLines);
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.styleFindingReport(anyString(), any(Sentiment.class)),
                Mockito.times(expectedRawLines.size()));
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.toNewLine(anyString()), Mockito.times(expectedRawLines.size()));
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereAreNoFindings() {
        // Given
        final var findings = TestUtil.createImmutableListOfFindings();
        final var expectedRawLines =
                List.of("No idle DynamoDB tables found that has been idle longer than max idle period.");

        // When
        final var actualResult = testObject.styleForPlainText(findings);

        // Then
        assertThat(actualResult).containsSubsequence(expectedRawLines);
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.styleFindingReport(anyString(), any(Sentiment.class)),
                Mockito.times(expectedRawLines.size()));
        mockedPlainTextReportStyler.verify(
                () -> PlainTextReportStyler.toNewLine(anyString()), Mockito.times(expectedRawLines.size()));
    }
}
