package com.lyr.rule.dynamodb.report;

import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.RuleReport;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;

public class ScanDynamodbTableIdleRuleReport implements RuleReport<ScanDynamodbTableIdleRuleConfig> {
    @Override
    public String reportToConsole(
            final ScanDynamodbTableIdleRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {
        final var maxIdlePeriodInDays = ruleConfig.getMaxIdlePeriodInDays();
        final var scanOutcomes = (List<ScanOutcome>) outcomes;

        if (outcomes.isEmpty()) {
            final var outcomeReport = String.format(
                    "No idle DynamoDB table found that are idle longer than %d days.", maxIdlePeriodInDays);
            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, Sentiment.POSITIVE);
            return ConsoleReportStyler.toNewLine(styledOutcomeReport);
        }

        final var reportBuilder = new StringBuilder();
        for (final ScanOutcome outcome : scanOutcomes) {
            final var outcomeReport = String.format(
                    "DynamoDB table '%s' has been idle for more than %d days.", outcome.result(), maxIdlePeriodInDays);
            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, Sentiment.NEGATIVE);

            reportBuilder.append(ConsoleReportStyler.toNewLine(styledOutcomeReport));
        }

        return reportBuilder.toString();
    }
}
