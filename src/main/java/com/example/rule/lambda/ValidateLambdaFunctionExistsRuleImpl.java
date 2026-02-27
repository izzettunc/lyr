package com.example.rule.lambda;

import com.example.rule.RuleStrategy;
import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;

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
