package com.lyr.rule.lambda.report;

import static com.lyr.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;

import com.lyr.rule.RuleReport;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleReport
        implements RuleReport<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    public String report(
            final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ignored,
            final List<? extends Outcome> outcomes) {
        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder
                .append(String.format(block))
                .append(String.format("%nValidation Report for "))
                .append(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY)
                .append(String.format(block));

        if (outcomes.isEmpty()) {
            reportBuilder.append(String.format("%n- [✅] No lambda found with unbounded concurrency."));
            return reportBuilder.toString();
        }

        for (final Outcome outcome : outcomes) {
            reportBuilder.append(String.format(
                    "%n- [❌] Lambda function '%s' has unbounded concurrency.", ((ScanOutcome) outcome).result()));
        }

        return reportBuilder.toString();
    }
}
