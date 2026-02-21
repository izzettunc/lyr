package com.example.rule.glue.report;

import com.example.rule.RuleReport;
import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.google.common.collect.ImmutableList;

import static com.example.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

public class ScanGlueSessionActiveWithLongIdleTimeoutRuleReport
        implements RuleReport<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    @Override
    public String report(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig, ImmutableList<? extends Outcome> outcomes) {
        var maxIdleTimeoutInMinutes = ruleConfig.getMaxIdleTimeoutInMinutes();

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format("%n========================"));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
        reportBuilder.append(String.format("%n========================"));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format("%n- [✅] No active sessions found with idle timeout greater than %d minutes.",
                    maxIdleTimeoutInMinutes));
            return reportBuilder.toString();
        }

        for (int i = 0; i < outcomes.size(); i++) {
            var outcome = (ScanOutcome) outcomes.get(i);

            reportBuilder.append(String.format("%n- [❌] Session '%s' has been active for more than idle timeout of %d minutes.",
                    outcome.result(), maxIdleTimeoutInMinutes));

        }

        return reportBuilder.toString();
    }
}
