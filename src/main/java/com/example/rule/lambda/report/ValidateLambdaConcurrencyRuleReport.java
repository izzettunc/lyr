package com.example.rule.lambda.report;

import com.example.rule.RuleReport;
import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.lambda.LambdaReason;
import com.google.common.collect.ImmutableList;

import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;

public class ValidateLambdaConcurrencyRuleReport implements RuleReport<ValidateLambdaFunctionConcurrencyRuleConfig> {

    @Override
    public String report(ValidateLambdaFunctionConcurrencyRuleConfig ruleConfig, ImmutableList<? extends Outcome> outcomes) {

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format("%n========================"));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY);
        reportBuilder.append(String.format("%n========================"));

        for (int i = 0; i < outcomes.size(); i++) {
            var functionName = ruleConfig.getFunctionConcurrences().get(i).functionName();
            var outcome = (ValidationOutcome<LambdaReason>) outcomes.get(i);

            if (outcome.success()){
                reportBuilder.append(String.format("%n- [✅] Lambda function '%s' has expected concurrency.", functionName));
            } else{
                reportBuilder.append(String.format("%n- [❌] Lambda function '%s' validation failed: %s", functionName, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
