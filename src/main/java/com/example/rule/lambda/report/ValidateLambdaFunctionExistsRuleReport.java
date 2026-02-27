package com.example.rule.lambda.report;

import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;

import com.example.rule.RuleReport;
import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import java.util.List;

public class ValidateLambdaFunctionExistsRuleReport implements RuleReport<ValidateLambdaFunctionExistsRuleConfig> {

    @Override
    public String report(
            final ValidateLambdaFunctionExistsRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder
                .append(String.format(block))
                .append(String.format("%nValidation Report for "))
                .append(VALIDATE_LAMBDA_FUNCTION_EXISTS)
                .append(String.format(block));

        for (int i = 0; i < outcomes.size(); i++) {
            final var functionName = ruleConfig.getFunctionNames().get(i);
            final var outcome = (ValidationOutcome<LambdaReason>) outcomes.get(i);

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
