package com.lyr.rule.glue.report;

import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.report.console.Sentiment;
import com.lyr.rule.RuleReport;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;

public class ScanGlueSessionActiveWithLongIdleTimeoutRuleReport
        implements RuleReport<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    @Override
    public String reportToConsole(
            final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig,
            final List<? extends Outcome> outcomes) {
        final var maxIdleTimeoutInMinutes = ruleConfig.getMaxIdleTimeoutInMinutes();
        final var scanOutcomes = (List<ScanOutcome>) outcomes;

        if (scanOutcomes.isEmpty()) {
            final var outcomeReport = String.format(
                    "No active sessions found with idle timeout greater than %d minutes.", maxIdleTimeoutInMinutes);
            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, Sentiment.POSITIVE);
            return ConsoleReportStyler.toNewLine(styledOutcomeReport);
        }

        final var reportBuilder = new StringBuilder();
        for (final ScanOutcome outcome : scanOutcomes) {
            final var outcomeReport = String.format(
                    "Session '%s' has been active for more than idle timeout of %d minutes.",
                    outcome.result(), maxIdleTimeoutInMinutes);
            final var styledOutcome = ConsoleReportStyler.styleOutcome(outcomeReport, Sentiment.NEGATIVE);
            reportBuilder.append(ConsoleReportStyler.toNewLine(styledOutcome));
        }

        return reportBuilder.toString();
    }
}
