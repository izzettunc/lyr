package com.lyr.report;

import static org.assertj.core.api.Assertions.assertThat;

import com.lyr.report.json.JsonReporter;
import com.lyr.report.plain.text.PlainTextReporter;
import org.junit.jupiter.api.Test;

class ReporterFactoryTest {

    @Test
    void testThatReporterFactoryCreatesAPlainTextReporterCorrectly() {
        // Given
        final var reportType = ReportType.PLAIN_TEXT;

        // When
        final var reporter = ReporterFactory.createReporter(reportType);

        // Then
        assertThat(reporter).isInstanceOf(PlainTextReporter.class);
    }

    @Test
    void testThatReporterFactoryCreatesAJsonReporterCorrectly() {
        // Given
        final var reportType = ReportType.JSON;

        // When
        final var reporter = ReporterFactory.createReporter(reportType);

        // Then
        assertThat(reporter).isInstanceOf(JsonReporter.class);
    }
}
