package com.lyr.report.console;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyr.report.ReportType;
import com.lyr.rule.Rule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsoleReporterTest {

    private static ConsoleReportStyler mockedStyler = mock(ConsoleReportStyler.class);
    private static ConsoleReporter testObject;

    @BeforeEach
    void beforeEach() {
        testObject = new ConsoleReporter(mockedStyler);
        reset(mockedStyler);
    }

    @Test
    void testThatConsoleReporterPrintsAppTitleBlock() {
        // Given
        final List<Rule> rules = List.of();

        // When
        testObject.report(rules);

        // Then
        verify(mockedStyler).buildTitleBlock(1, "LYR REPORT");
    }

    @Test
    void testThatConsoleReporterPrintsForEachRuleTheirReportAndTitleBlock() {
        // Given
        final var rules = List.of(mock(Rule.class), mock(Rule.class));

        // When
        for (int i = 0; i < rules.size(); i++) {
            final var mockedRule = rules.get(i);
            when(mockedRule.getRuleName()).thenReturn("dummy" + i);
            when(mockedRule.report(ReportType.CONSOLE)).thenReturn("dummy" + i);
        }

        testObject.report(rules);

        // Then
        verify(mockedStyler).buildTitleBlock(1, "LYR REPORT");
        for (int i = 0; i < rules.size(); i++) {
            final var mockedRule = rules.get(i);
            verify(mockedStyler).buildTitleBlock(2, "Report for dummy" + i);
            verify(mockedRule).report(ReportType.CONSOLE);
        }
    }
}
