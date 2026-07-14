package com.lyr.report.style.finding.dynamodb;

import com.lyr.report.model.Finding;
import com.lyr.report.plain.text.Sentiment;
import com.lyr.report.style.finding.FindingStyler;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import java.util.List;

public class ScanDynamodbTableIdleRuleFindingStyler implements FindingStyler {

    @Override
    public String styleForPlainText(final List<Finding> findings) {
        if (findings.isEmpty()) {
            final var findingReport = "No idle DynamoDB tables found that has been idle longer than max idle period.";
            final var styledFindingReport = PlainTextReportStyler.styleFindingReport(findingReport, Sentiment.POSITIVE);
            return PlainTextReportStyler.toNewLine(styledFindingReport);
        }

        final var reportBuilder = new StringBuilder();
        for (final Finding finding : findings) {
            final var findingReport = String.format(
                    "DynamoDB table '%s' has been idle longer than max idle period.", finding.identifier());
            final var styledFindingReport = PlainTextReportStyler.styleFindingReport(findingReport, Sentiment.NEGATIVE);

            reportBuilder.append(PlainTextReportStyler.toNewLine(styledFindingReport));
        }

        return reportBuilder.toString();
    }
}
