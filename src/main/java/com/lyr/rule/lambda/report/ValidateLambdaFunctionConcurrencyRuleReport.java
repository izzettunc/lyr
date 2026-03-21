package com.lyr.rule.lambda.report;

import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;

import com.lyr.rule.RuleReport;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import java.util.List;

public class ValidateLambdaFunctionConcurrencyRuleReport
        implements RuleReport<ValidateLambdaFunctionConcurrencyRuleConfig> {

    @Override
    public String report(
            final ValidateLambdaFunctionConcurrencyRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder
                .append(String.format(block))
                .append(String.format("%nValidation Report for "))
                .append(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY)
                .append(String.format(block));

        for (int i = 0; i < outcomes.size(); i++) {
            final var functionName = ruleConfig.getFunctionConcurrences().get(i).functionName();
            final var outcome = (ValidationOutcome<LambdaReason>) outcomes.get(i);

            if (outcome.success()) {
                reportBuilder.append(
                        String.format("%n- [✅] Lambda function '%s' has expected concurrency.", functionName));
            } else {
                reportBuilder.append(String.format(
                        "%n- [❌] Lambda function '%s' validation failed: %s", functionName, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
