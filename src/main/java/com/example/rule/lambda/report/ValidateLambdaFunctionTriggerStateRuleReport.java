package com.example.rule.lambda.report;

import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;

import com.example.rule.RuleReport;
import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.google.common.collect.ImmutableList;

public class ValidateLambdaFunctionTriggerStateRuleReport
        implements RuleReport<ValidateLambdaFunctionTriggerStateRuleConfig> {

    @Override
    public String report(
            final ValidateLambdaFunctionTriggerStateRuleConfig ruleConfig,
            final ImmutableList<? extends Outcome> outcomes) {

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder.append(String.format(block));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE);
        reportBuilder.append(String.format(block));

        for (int i = 0; i < outcomes.size(); i++) {
            final var functionName =
                    ruleConfig.getFunctionTriggerStates().get(i).functionName();
            final var expectedState =
                    ruleConfig.getFunctionTriggerStates().get(i).enabled() ? "enabled" : "disabled";
            final var outcome = (ValidationOutcome<LambdaReason>) outcomes.get(i);

            if (outcome.success()) {
                reportBuilder.append(String.format(
                        "%n- [✅] Lambda function '%s' expected to be %s. Validation passed: %s",
                        functionName, expectedState, outcome.reason()));
            } else {
                reportBuilder.append(String.format(
                        "%n- [❌] Lambda function '%s' expected to be %s. Validation failed: %s",
                        functionName, expectedState, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
