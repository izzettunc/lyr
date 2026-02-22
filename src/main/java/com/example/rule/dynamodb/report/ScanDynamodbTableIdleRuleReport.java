package com.example.rule.dynamodb.report;

import static com.example.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;

import com.example.rule.RuleReport;
import com.example.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.google.common.collect.ImmutableList;

public class ScanDynamodbTableIdleRuleReport implements RuleReport<ScanDynamodbTableIdleRuleConfig> {

    @Override
    public String report(
            final ScanDynamodbTableIdleRuleConfig ruleConfig, final ImmutableList<? extends Outcome> outcomes) {
        final var maxIdlePeriodInDays = (int) ruleConfig.getMaxIdlePeriodInDays();

        final var reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder.append(String.format(block));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(SCAN_DYNAMODB_TABLE_IDLE);
        reportBuilder.append(String.format(block));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format(
                    "%n- [✅] No idle dynamodb table found that are idle longer than %d days.", maxIdlePeriodInDays));
            return reportBuilder.toString();
        }

        for (int i = 0; i < outcomes.size(); i++) {
            final var outcome = (ScanOutcome) outcomes.get(i);

            reportBuilder.append(String.format(
                    "%n- [❌] Dynamodb table '%s' has been idle for more than %d days.",
                    outcome.result(), maxIdlePeriodInDays));
        }

        return reportBuilder.toString();
    }
}
