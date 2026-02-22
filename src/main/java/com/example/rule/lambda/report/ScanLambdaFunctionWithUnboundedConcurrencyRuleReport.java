package com.example.rule.lambda.report;

import static com.example.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;

import com.example.rule.RuleReport;
import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.google.common.collect.ImmutableList;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleReport
        implements RuleReport<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    public String report(
            final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ignored,
            final ImmutableList<? extends Outcome> outcomes) {
        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder.append(String.format(block));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY);
        reportBuilder.append(String.format(block));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format("%n- [✅] No lambda found with unbounded concurrency."));
            return reportBuilder.toString();
        }

        for (int i = 0; i < outcomes.size(); i++) {
            final var outcome = (ScanOutcome) outcomes.get(i);

            reportBuilder.append(
                    String.format("%n- [❌] Lambda function '%s' has unbounded concurrency.", outcome.result()));
        }

        return reportBuilder.toString();
    }
}
