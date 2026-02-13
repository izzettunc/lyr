package com.example.rule.lambda.report;

import com.example.rule.RuleReport;
import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.lambda.LambdaReason;
import com.google.common.collect.ImmutableList;

public class ValidateLambdaFunctionTriggerStateRuleReport implements RuleReport<ValidateLambdaFunctionTriggerStateRuleConfig> {

    @Override
    public String report(ValidateLambdaFunctionTriggerStateRuleConfig ruleConfig, ImmutableList<? extends Outcome> outcomes) {

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format("%n========================"));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(ValidateLambdaFunctionTriggerStateRuleConfig.NAME);
        reportBuilder.append(String.format("%n========================"));


        for (int i = 0; i < outcomes.size(); i++) {
            var functionName = ruleConfig.getFunctionTriggerStates().get(i).functionName();
            var expectedState = ruleConfig.getFunctionTriggerStates().get(i).enabled() ? "enabled" : "disabled";
            var outcome = (ValidationOutcome<LambdaReason>) outcomes.get(i);

            if (outcome.success()){
                reportBuilder.append(String.format("%n- [✅] Lambda function '%s' expected to be %s. Validation passed: %s", functionName, expectedState, outcome.reason()));
            } else{
                reportBuilder.append(String.format("%n- [❌] Lambda function '%s' expected to be %s. Validation failed: %s", functionName, expectedState, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
