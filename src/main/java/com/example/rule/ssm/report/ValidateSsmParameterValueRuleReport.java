package com.example.rule.ssm.report;

import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;

import com.example.rule.RuleReport;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.SsmReason;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import com.google.common.collect.ImmutableList;

public class ValidateSsmParameterValueRuleReport implements RuleReport<ValidateSsmParameterValueRuleConfig> {

    @Override
    public String report(ValidateSsmParameterValueRuleConfig ruleConfig, ImmutableList<? extends Outcome> outcomes) {

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format("%n========================"));
        reportBuilder.append(String.format("%nValidation Report for "));
        reportBuilder.append(VALIDATE_SSM_PARAMETER_VALUE);
        reportBuilder.append(String.format("%n========================"));

        for (int i = 0; i < outcomes.size(); i++) {
            var parameterName = ruleConfig.getParameterValues().get(i).parameterName();
            var outcome = (ValidationOutcome<SsmReason>) outcomes.get(i);

            if (outcome.success()) {
                reportBuilder.append(String.format("%n- [✅] Ssm parameter '%s' has expected value.", parameterName));
            } else {
                reportBuilder.append(String.format(
                        "%n- [❌] Ssm parameter '%s' validation failed: %s", parameterName, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
