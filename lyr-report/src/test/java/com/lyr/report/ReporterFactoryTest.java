package com.lyr.report;

import static org.assertj.core.api.Assertions.assertThat;

import com.lyr.report.console.ConsoleReporter;
import org.junit.jupiter.api.Test;

class ReporterFactoryTest {

    @Test
    void testThatReporterFactoryCreatesAConsoleReporterCorrectly() {
        // Given
        final var reportType = ReportType.CONSOLE;

        // When
        final var reporter = ReporterFactory.createReporter(reportType);

        // Then
        assertThat(reporter).isInstanceOf(ConsoleReporter.class);
    }
}
