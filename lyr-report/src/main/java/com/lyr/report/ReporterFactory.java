package com.lyr.report;

import com.lyr.report.console.ConsoleReporter;
import com.lyr.report.style.console.ConsoleReportStyler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReporterFactory {

    public static Reporter createReporter(final ReportType reportType) {
        return switch (reportType) {
            case CONSOLE -> new ConsoleReporter(new ConsoleReportStyler());
        };
    }
}
