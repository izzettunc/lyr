package com.lyr.report.console;

import static com.lyr.report.TestUtil.createImmutableListOfFindings;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.lyr.report.model.Execution;
import com.lyr.report.style.FindingStyler;
import com.lyr.report.style.StylerFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ConsoleReporterTest {

    private static ConsoleReportStyler mockedConsoleReportStyler = mock(ConsoleReportStyler.class);
    private static FindingStyler mockedFindingStyler = mock(FindingStyler.class);
    private static MockedStatic<StylerFactory> mockedStylerFactory = mockStatic(StylerFactory.class);
    private static ConsoleReporter testObject;

    @BeforeEach
    void beforeEach() {
        testObject = new ConsoleReporter(mockedConsoleReportStyler);
        reset(mockedConsoleReportStyler, mockedFindingStyler);
        mockedStylerFactory.reset();
    }

    @Test
    void testThatConsoleReporterPrintsAppTitleBlock() {
        // Given
        final List<Execution> executions = List.of();

        // When
        testObject.report(executions);

        // Then
        verify(mockedConsoleReportStyler).buildTitleBlock(1, "LYR REPORT");
    }

    @Test
    void testThatConsoleReporterPrintsForEachRuleTheirReportAndTitleBlock() {
        // Given
        final var executions = List.of(
                Execution.builder()
                        .name("dummy0")
                        .code("d0")
                        .findings(createImmutableListOfFindings("id0", "id1"))
                        .build(),
                Execution.builder()
                        .name("dummy1")
                        .code("d1")
                        .findings(createImmutableListOfFindings("id2"))
                        .build());

        // When
        mockedStylerFactory.when(() -> StylerFactory.getStylerFor(any())).thenReturn(mockedFindingStyler);
        testObject.report(executions);

        // Then
        verify(mockedConsoleReportStyler).buildTitleBlock(1, "LYR REPORT");
        for (int i = 0; i < executions.size(); i++) {
            final var execution = executions.get(i);
            verify(mockedConsoleReportStyler).buildTitleBlock(2, "Report for dummy" + i);
            verify(mockedFindingStyler).styleForConsole(execution.findings());
        }
    }
}
