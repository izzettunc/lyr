package com.example.rule.glue.report;

import static com.example.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

import com.example.rule.RuleReport;
import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.google.common.collect.ImmutableList;

public class ScanGlueSessionActiveWithLongIdleTimeoutRuleReport
        implements RuleReport<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    @Override
    public String report(
            final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig,
            final ImmutableList<? extends Outcome> outcomes) {
        final var maxIdleTimeoutInMinutes = ruleConfig.getMaxIdleTimeoutInMinutes();

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder.append(String.format(block));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
        reportBuilder.append(String.format(block));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format(
                    "%n- [✅] No active sessions found with idle timeout greater than %d minutes.",
                    maxIdleTimeoutInMinutes));
            return reportBuilder.toString();
        }

        for (int i = 0; i < outcomes.size(); i++) {
            final var outcome = (ScanOutcome) outcomes.get(i);

            reportBuilder.append(String.format(
                    "%n- [❌] Session '%s' has been active for more than idle timeout of %d minutes.",
                    outcome.result(), maxIdleTimeoutInMinutes));
        }

        return reportBuilder.toString();
    }
}
