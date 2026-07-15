package com.lyr.report.style.finding.lambda;

import com.lyr.report.model.Finding;
import com.lyr.report.plain.text.Sentiment;
import com.lyr.report.style.finding.FindingStyler;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import java.util.List;

public class ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStyler implements FindingStyler {

    @Override
    public String styleForPlainText(final List<Finding> findings) {
        if (findings.isEmpty()) {
            final var findingReport = "No lambda functions found with X-Ray tracing not enabled.";
            final var styledFindingReport = PlainTextReportStyler.styleFindingReport(findingReport, Sentiment.POSITIVE);
            return PlainTextReportStyler.toNewLine(styledFindingReport);
        }

        final StringBuilder reportBuilder = new StringBuilder();
        for (final Finding finding : findings) {
            final var findingReport =
                    String.format("Lambda function '%s' has X-Ray tracing not enabled.", finding.identifier());
            final var styledFindingReport = PlainTextReportStyler.styleFindingReport(findingReport, Sentiment.NEGATIVE);
            reportBuilder.append(PlainTextReportStyler.toNewLine(styledFindingReport));
        }

        return reportBuilder.toString();
    }
}
