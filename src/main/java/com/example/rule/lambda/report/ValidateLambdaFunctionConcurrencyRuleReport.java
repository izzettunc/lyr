package com.example.rule.lambda.report;

import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;

import com.example.rule.RuleReport;
import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.google.common.collect.ImmutableList;

public class ValidateLambdaFunctionConcurrencyRuleReport
        implements RuleReport<ValidateLambdaFunctionConcurrencyRuleConfig> {

    @Override
    public String report(
            final ValidateLambdaFunctionConcurrencyRuleConfig ruleConfig,
            final ImmutableList<? extends Outcome> outcomes) {

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder.append(String.format(block));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY);
        reportBuilder.append(String.format(block));

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
