package com.lyr.report.style.finding.lambda;

import com.lyr.report.console.Sentiment;
import com.lyr.report.model.Finding;
import com.lyr.report.style.console.ConsoleReportStyler;
import com.lyr.report.style.finding.FindingStyler;
import java.util.List;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler implements FindingStyler {

    @Override
    public String styleForConsole(final List<Finding> findings) {
        if (findings.isEmpty()) {
            final var findingReport = "No lambda found with unbounded concurrency.";
            final var styledFindingReport = ConsoleReportStyler.styleFindingReport(findingReport, Sentiment.POSITIVE);
            return ConsoleReportStyler.toNewLine(styledFindingReport);
        }

        final StringBuilder reportBuilder = new StringBuilder();
        for (final Finding finding : findings) {
            final var findingReport =
                    String.format("Lambda function '%s' has unbounded concurrency.", finding.identifier());
            final var styledFindingReport = ConsoleReportStyler.styleFindingReport(findingReport, Sentiment.NEGATIVE);
            reportBuilder.append(ConsoleReportStyler.toNewLine(styledFindingReport));
        }

        return reportBuilder.toString();
    }
}
