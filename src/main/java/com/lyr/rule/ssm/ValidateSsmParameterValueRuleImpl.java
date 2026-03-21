package com.lyr.rule.ssm;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.RuleStrategy;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import com.lyr.services.ssm.SsmConnector;

public class ValidateSsmParameterValueRuleImpl implements RuleStrategy<ValidateSsmParameterValueRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(final ValidateSsmParameterValueRuleConfig parameters) {
        return parameters.getParameterValues().stream()
                .map(this::validateSsmParameterValue)
                .collect(ImmutableList.toImmutableList());
    }

    private ValidationOutcome<SsmReason> validateSsmParameterValue(
            final ValidateSsmParameterValueRuleConfig.SsmParameterValue parameterValue) {
        final var optSsmParameter = SsmConnector.create().getParameter(parameterValue.parameterName());

        if (optSsmParameter.isEmpty()) {
            return ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND);
        }

        if (!optSsmParameter.get().value().equalsIgnoreCase(parameterValue.value())) {
            return ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH);
        }

        return ValidationOutcome.valid();
    }
}
