package com.example.rule.ssm;

import com.example.rule.RuleStrategy;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import com.example.services.ssm.SsmConnector;
import com.google.common.collect.ImmutableList;

public class ValidateSsmParameterValueRuleImpl implements RuleStrategy<ValidateSsmParameterValueRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(ValidateSsmParameterValueRuleConfig parameters) {
        return parameters
                .getParameterValues()
                .stream()
                .map(parameterValue -> {
                    var optSsmParameter = SsmConnector.getInstance().getParameter(parameterValue.parameterName());

                    if (optSsmParameter.isEmpty()) {
                        return ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND);
                    }

                    if (!optSsmParameter.get().value().equalsIgnoreCase(parameterValue.value())) {
                        return ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH);
                    }

                    return ValidationOutcome.valid();
                })
                .collect(ImmutableList.toImmutableList());
    }
}
