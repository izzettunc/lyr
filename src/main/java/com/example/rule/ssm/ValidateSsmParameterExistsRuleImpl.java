package com.example.rule.ssm;

import com.example.rule.RuleStrategy;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.example.services.ssm.SsmConnector;
import com.google.common.collect.ImmutableList;

public class ValidateSsmParameterExistsRuleImpl implements RuleStrategy<ValidateSsmParameterExistsRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(final ValidateSsmParameterExistsRuleConfig parameters) {
        return parameters.getParameterNames().stream()
                .map(parameterName -> {
                    final var optSsmParameter = SsmConnector.create().getParameter(parameterName);

                    if (optSsmParameter.isEmpty()) {
                        return ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND);
                    }

                    return ValidationOutcome.valid();
                })
                .collect(ImmutableList.toImmutableList());
    }
}
