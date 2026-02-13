package com.example.rule.lambda.report;

import com.example.rule.RuleReport;
import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.google.common.collect.ImmutableList;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleReport
        implements RuleReport<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    public String report(ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ignored, ImmutableList<? extends Outcome> outcomes) {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format("%n========================"));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.NAME);
        reportBuilder.append(String.format("%n========================"));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format("%n- [✅] No lambda found with unbounded concurrency."));
            return reportBuilder.toString();
        }

        for (int i = 0; i < outcomes.size(); i++) {
            var outcome = (ScanOutcome) outcomes.get(i);

            reportBuilder.append(String.format("%n- [❌] Lambda function '%s' has unbounded concurrency.", outcome.result()));
        }

        return reportBuilder.toString();
    }
}
