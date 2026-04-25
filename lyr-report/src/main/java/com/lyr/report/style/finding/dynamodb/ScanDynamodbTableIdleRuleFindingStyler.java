package com.lyr.report.style.finding.dynamodb;

import com.lyr.report.console.Sentiment;
import com.lyr.report.model.Finding;
import com.lyr.report.style.console.ConsoleReportStyler;
import com.lyr.report.style.finding.FindingStyler;
import java.util.List;

public class ScanDynamodbTableIdleRuleFindingStyler implements FindingStyler {

    @Override
    public String styleForConsole(final List<Finding> findings) {
        if (findings.isEmpty()) {
            final var findingReport = "No idle DynamoDB table found that has been idle longer than max idle period.";
            final var styledFindingReport = ConsoleReportStyler.styleFindingReport(findingReport, Sentiment.POSITIVE);
            return ConsoleReportStyler.toNewLine(styledFindingReport);
        }

        final var reportBuilder = new StringBuilder();
        for (final Finding finding : findings) {
            final var findingReport = String.format(
                    "DynamoDB table '%s' has been idle longer than max idle period.", finding.identifier());
            final var styledFindingReport = ConsoleReportStyler.styleFindingReport(findingReport, Sentiment.NEGATIVE);

            reportBuilder.append(ConsoleReportStyler.toNewLine(styledFindingReport));
        }

        return reportBuilder.toString();
    }
}
