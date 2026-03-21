package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.RuleStrategy;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.services.lambda.LambdaConnector;

public class ValidateLambdaFunctionExistsRuleImpl implements RuleStrategy<ValidateLambdaFunctionExistsRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(final ValidateLambdaFunctionExistsRuleConfig parameters) {
        return parameters.getFunctionNames().stream()
                .map(lambdaFunctionName -> {
                    final var optLambdaFunction = LambdaConnector.create().getLambdaFunction(lambdaFunctionName);

                    if (optLambdaFunction.isEmpty()) {
                        return ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND);
                    }

                    return ValidationOutcome.valid();
                })
                .collect(ImmutableList.toImmutableList());
    }
}
