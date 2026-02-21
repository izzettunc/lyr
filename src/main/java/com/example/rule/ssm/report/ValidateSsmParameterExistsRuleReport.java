package com.example.rule.ssm.report;

import com.example.rule.RuleReport;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.example.rule.ssm.SsmReason;
import com.google.common.collect.ImmutableList;

import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;

public class ValidateSsmParameterExistsRuleReport implements RuleReport<ValidateSsmParameterExistsRuleConfig> {

    @Override
    public String report(ValidateSsmParameterExistsRuleConfig ruleConfig, ImmutableList<? extends Outcome> outcomes) {

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format("%n========================"));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(VALIDATE_SSM_PARAMETER_EXISTS);
        reportBuilder.append(String.format("%n========================"));

        for (int i = 0; i < outcomes.size(); i++) {
            var parameterName = ruleConfig.getParameterNames().get(i);
            var outcome = (ValidationOutcome<SsmReason>) outcomes.get(i);

            if (outcome.success()){
                reportBuilder.append(String.format("%n- [✅] Ssm parameter '%s' exists.", parameterName));
            } else {
                reportBuilder.append(String.format("%n- [❌] Ssm parameter '%s' validation failed: %s", parameterName, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
