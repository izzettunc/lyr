package com.lyr.rule.lambda.report;

import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;

import com.lyr.rule.RuleReport;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import java.util.List;

public class ValidateLambdaFunctionTriggerStateRuleReport
        implements RuleReport<ValidateLambdaFunctionTriggerStateRuleConfig> {

    @Override
    public String report(
            final ValidateLambdaFunctionTriggerStateRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder
                .append(String.format(block))
                .append(String.format("%nValidation Report for "))
                .append(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE)
                .append(String.format(block));

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
