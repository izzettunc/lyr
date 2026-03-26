package com.lyr.rule.ssm.report;

import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;

import com.lyr.rule.RuleReport;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.rule.ssm.SsmReason;
import com.lyr.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import java.util.List;

public class ValidateSsmParameterExistsRuleReport implements RuleReport<ValidateSsmParameterExistsRuleConfig> {

    @Override
    public String report(
            final ValidateSsmParameterExistsRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {

        final StringBuilder reportBuilder = new StringBuilder();
        final var block = "%n========================";
        reportBuilder
                .append(String.format(block))
                .append(String.format("%nValidation Report for "))
                .append(VALIDATE_SSM_PARAMETER_EXISTS)
                .append(String.format(block));

        for (int i = 0; i < outcomes.size(); i++) {
            final var parameterName = ruleConfig.getParameterNames().get(i);
            final var outcome = (ValidationOutcome<SsmReason>) outcomes.get(i);

            if (outcome.success()) {
                reportBuilder.append(String.format("%n- [✅] Ssm parameter '%s' exists.", parameterName));
            } else {
                reportBuilder.append(String.format(
                        "%n- [❌] Ssm parameter '%s' validation failed: %s", parameterName, outcome.reason()));
            }
        }

        return reportBuilder.toString();
    }
}
