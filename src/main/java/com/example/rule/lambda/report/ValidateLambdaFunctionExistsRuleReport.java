package com.example.rule.lambda.report;

import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;

import com.example.rule.RuleReport;
import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.google.common.collect.ImmutableList;

public class ValidateLambdaFunctionExistsRuleReport implements RuleReport<ValidateLambdaFunctionExistsRuleConfig> {

    @Override
    public String report(ValidateLambdaFunctionExistsRuleConfig ruleConfig, ImmutableList<? extends Outcome> outcomes) {

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format("%n========================"));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(VALIDATE_LAMBDA_FUNCTION_EXISTS);
        reportBuilder.append(String.format("%n========================"));

        for (int i = 0; i < outcomes.size(); i++) {
            var functionName = ruleConfig.getFunctionNames().get(i);
            var outcome = (ValidationOutcome<LambdaReason>) outcomes.get(i);

            if (outcome.success()) {
                reportBuilder.append(String.format("%n- [✅] Lambda function '%s' exists.", functionName));
            } else {
                reportBuilder.append(String.format(
                        "%n- [❌] Lambda function '%s' validation failed: %s", functionName, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
