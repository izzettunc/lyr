package com.lyr.rule.glue.report;

import static com.lyr.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

import com.lyr.rule.RuleReport;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;

public class ScanGlueSessionActiveWithLongIdleTimeoutRuleReport
        implements RuleReport<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    @Override
    public String report(
            final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig,
            final List<? extends Outcome> outcomes) {
        final var maxIdleTimeoutInMinutes = ruleConfig.getMaxIdleTimeoutInMinutes();

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder
                .append(String.format(block))
                .append(String.format("%nValidation Report for "))
                .append(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT)
                .append(String.format(block));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format(
                    "%n- [✅] No active sessions found with idle timeout greater than %d minutes.",
                    maxIdleTimeoutInMinutes));
            return reportBuilder.toString();
        }

        for (final Outcome outcome : outcomes) {
            reportBuilder.append(String.format(
                    "%n- [❌] Session '%s' has been active for more than idle timeout of %d minutes.",
                    ((ScanOutcome) outcome).result(), maxIdleTimeoutInMinutes));
        }

        return reportBuilder.toString();
    }
}
