package com.lyr.report.plain.text;

import static com.lyr.report.TestUtil.createImmutableListOfFindings;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyr.report.io.OutputStrategy;
import com.lyr.report.model.Execution;
import com.lyr.report.style.finding.FindingStyler;
import com.lyr.report.style.finding.StylerFactory;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import com.lyr.report.style.plain.text.TitleLevel;
import com.lyr.util.RuleDefinition;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class PlainTextReporterTest {

    private static PlainTextReportStyler mockedPlainTextReportStyler = mock(PlainTextReportStyler.class);
    private static OutputStrategy mockedOutputStrategy = mock(OutputStrategy.class);
    private static FindingStyler mockedFindingStyler = mock(FindingStyler.class);
    private static MockedStatic<StylerFactory> mockedStylerFactory = mockStatic(StylerFactory.class);
    private static PlainTextReporter testObject;

    @BeforeEach
    void beforeEach() {
        testObject = new PlainTextReporter(mockedPlainTextReportStyler, mockedOutputStrategy);
        reset(mockedPlainTextReportStyler, mockedFindingStyler, mockedOutputStrategy);
        mockedStylerFactory.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedStylerFactory.closeOnDemand();
    }

    @Test
    void testThatPlainTextReporterPrintsAppTitleBlock() {
        // Given
        final List<Execution> executions = List.of();

        // When
        when(mockedPlainTextReportStyler.buildTitleBlock(TitleLevel.PRIMARY, "LYR REPORT"))
                .thenReturn("dummy");
        testObject.report(executions);

        // Then
        verify(mockedPlainTextReportStyler, times(1)).buildTitleBlock(TitleLevel.PRIMARY, "LYR REPORT");
        verify(mockedOutputStrategy, times(1)).write("dummy");
    }

    @Test
    void testThatPlainTextReporterPrintsForEachRuleTheirReportAndTitleBlock() {
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
        try (MockedStatic<RuleDefinition> _ = mockStatic(RuleDefinition.class)) {
            mockedStylerFactory.when(() -> StylerFactory.getStylerFor(any())).thenReturn(mockedFindingStyler);
            when(mockedPlainTextReportStyler.buildTitleBlock(eq(TitleLevel.PRIMARY), any()))
                    .thenReturn("dummyPrimaryTitle");
            when(mockedPlainTextReportStyler.buildTitleBlock(eq(TitleLevel.SECONDARY), any()))
                    .thenReturn("dummySecondaryTitle");
            when(mockedFindingStyler.styleForPlainText(any())).thenReturn("dummyFindings");
            testObject.report(executions);

            // Then
            verify(mockedPlainTextReportStyler, times(1)).buildTitleBlock(TitleLevel.PRIMARY, "LYR REPORT");
            for (int i = 0; i < executions.size(); i++) {
                final var execution = executions.get(i);
                verify(mockedPlainTextReportStyler, times(1))
                        .buildTitleBlock(TitleLevel.SECONDARY, "Report for dummy" + i);
                verify(mockedFindingStyler, times(1)).styleForPlainText(execution.findings());
            }
            verify(mockedOutputStrategy, times(1)).write("dummyPrimaryTitle");
            verify(mockedOutputStrategy, times(executions.size())).write("dummySecondaryTitle");
            verify(mockedOutputStrategy, times(executions.size())).write("dummyFindings");
        }
    }
}
