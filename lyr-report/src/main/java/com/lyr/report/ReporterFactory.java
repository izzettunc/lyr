package com.lyr.report;

import com.lyr.report.io.ConsolePrinter;
import com.lyr.report.json.JsonReporter;
import com.lyr.report.plain.text.PlainTextReporter;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReporterFactory {

    public static Reporter createReporter(final ReportType reportType) {
        return switch (reportType) {
            case PLAIN_TEXT -> new PlainTextReporter(new PlainTextReportStyler(), new ConsolePrinter());
            case JSON -> new JsonReporter(new ConsolePrinter());
        };
    }
}
