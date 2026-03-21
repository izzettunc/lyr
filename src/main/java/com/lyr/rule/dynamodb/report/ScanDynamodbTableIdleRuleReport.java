package com.lyr.rule.dynamodb.report;

import static com.lyr.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;

import com.lyr.rule.RuleReport;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;

public class ScanDynamodbTableIdleRuleReport implements RuleReport<ScanDynamodbTableIdleRuleConfig> {

    @Override
    public String report(final ScanDynamodbTableIdleRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {
        final var maxIdlePeriodInDays = (int) ruleConfig.getMaxIdlePeriodInDays();

        final var reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder
                .append(String.format(block))
                .append(String.format("%nValidation Report for "))
                .append(SCAN_DYNAMODB_TABLE_IDLE)
                .append(String.format(block));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format(
                    "%n- [✅] No idle dynamodb table found that are idle longer than %d days.", maxIdlePeriodInDays));
            return reportBuilder.toString();
        }

        for (final Outcome outcome : outcomes) {

            reportBuilder.append(String.format(
                    "%n- [❌] Dynamodb table '%s' has been idle for more than %d days.",
                    ((ScanOutcome) outcome).result(), maxIdlePeriodInDays));
        }

        return reportBuilder.toString();
    }
}
