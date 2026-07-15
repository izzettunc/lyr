package com.lyr.report.style.finding.lambda;

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

class ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStylerTest {

    static final MockedStatic<PlainTextReportStyler> mockedPlainTextReportStyler =
            mockStatic(PlainTextReportStyler.class, Mockito.CALLS_REAL_METHODS);
    ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStyler testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStyler();
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
                "Lambda function 'dummy' has X-Ray tracing not enabled.",
                "Lambda function 'dummy2' has X-Ray tracing not enabled.");

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
        final var expectedRawLines = List.of("No lambda functions found with X-Ray tracing not enabled.");

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
