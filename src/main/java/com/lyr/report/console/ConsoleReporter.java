package com.lyr.report.console;

import com.lyr.report.ReportType;
import com.lyr.report.Reporter;
import com.lyr.rule.Rule;
import java.util.List;

public class ConsoleReporter implements Reporter {
    private final ConsoleReportStyler styler;

    public ConsoleReporter(final ConsoleReportStyler consoleReportStyler) {
        this.styler = consoleReportStyler;
    }

    @Override
    public void report(final List<Rule> rules) {
        System.out.println(styler.buildTitleBlock(1, "LYR REPORT"));

        for (final Rule rule : rules) {
            System.out.println(styler.buildTitleBlock(2, "Report for " + rule.getRuleName()));
            System.out.println(rule.report(ReportType.CONSOLE));
            System.out.println();
        }
    }
}
