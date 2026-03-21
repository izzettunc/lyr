package com.lyr.rule.ssm;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.RuleStrategy;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.lyr.services.ssm.SsmConnector;

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
