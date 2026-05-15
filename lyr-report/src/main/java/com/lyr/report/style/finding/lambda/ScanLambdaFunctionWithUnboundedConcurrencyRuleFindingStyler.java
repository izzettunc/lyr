package com.lyr.report.style.finding.lambda;

import com.lyr.report.model.Finding;
import com.lyr.report.plain.text.Sentiment;
import com.lyr.report.style.finding.FindingStyler;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import java.util.List;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler implements FindingStyler {

    @Override
    public String styleForPlainText(final List<Finding> findings) {
        if (findings.isEmpty()) {
            final var findingReport = "No lambda found with unbounded concurrency.";
            final var styledFindingReport = PlainTextReportStyler.styleFindingReport(findingReport, Sentiment.POSITIVE);
            return PlainTextReportStyler.toNewLine(styledFindingReport);
        }

        final StringBuilder reportBuilder = new StringBuilder();
        for (final Finding finding : findings) {
            final var findingReport =
                    String.format("Lambda function '%s' has unbounded concurrency.", finding.identifier());
            final var styledFindingReport = PlainTextReportStyler.styleFindingReport(findingReport, Sentiment.NEGATIVE);
            reportBuilder.append(PlainTextReportStyler.toNewLine(styledFindingReport));
        }

        return reportBuilder.toString();
    }
}
