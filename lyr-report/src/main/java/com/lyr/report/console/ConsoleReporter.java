package com.lyr.report.console;

import com.lyr.report.Reporter;
import com.lyr.report.model.Execution;
import com.lyr.report.style.console.ConsoleReportStyler;
import com.lyr.report.style.console.TitleLevel;
import com.lyr.report.style.finding.StylerFactory;
import com.lyr.util.RuleDefinition;
import java.util.List;

public class ConsoleReporter implements Reporter {
    private final ConsoleReportStyler styler;

    public ConsoleReporter(final ConsoleReportStyler consoleReportStyler) {
        this.styler = consoleReportStyler;
    }

    @Override
    public void report(final List<Execution> executions) {
        System.out.println(styler.buildTitleBlock(TitleLevel.PRIMARY, "LYR REPORT"));

        for (final Execution execution : executions) {
            final var findingStyler = StylerFactory.getStylerFor(RuleDefinition.definitionByName(execution.name()));

            System.out.println(styler.buildTitleBlock(TitleLevel.SECONDARY, "Report for " + execution.name()));
            System.out.println("Execution configuration:");
            System.out.println(styler.styleExecutionConfiguration(execution.configuration()));
            System.out.println(findingStyler.styleForConsole(execution.findings()));
            System.out.println();
        }
    }
}
