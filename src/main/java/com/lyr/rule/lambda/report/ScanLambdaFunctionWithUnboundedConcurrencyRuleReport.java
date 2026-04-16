package com.lyr.rule.lambda.report;

import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.RuleReport;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleReport
        implements RuleReport<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    public String reportToConsole(
            final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ignored,
            final List<? extends Outcome> outcomes) {
        final List<ScanOutcome> scanOutcomes = (List<ScanOutcome>) outcomes;

        if (scanOutcomes.isEmpty()) {
            final var outcomeReport = "No lambda found with unbounded concurrency.";
            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, Sentiment.POSITIVE);
            return ConsoleReportStyler.toNewLine(styledOutcomeReport);
        }

        final StringBuilder reportBuilder = new StringBuilder();
        for (final ScanOutcome outcome : scanOutcomes) {
            final var outcomeReport =
                    String.format("Lambda function '%s' has unbounded concurrency.", outcome.result());
            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, Sentiment.NEGATIVE);
            reportBuilder.append(ConsoleReportStyler.toNewLine(styledOutcomeReport));
        }

        return reportBuilder.toString();
    }
}
