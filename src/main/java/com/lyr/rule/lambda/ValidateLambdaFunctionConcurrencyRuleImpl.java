package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.RuleStrategy;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.services.lambda.LambdaConnector;

public class ValidateLambdaFunctionConcurrencyRuleImpl
        implements RuleStrategy<ValidateLambdaFunctionConcurrencyRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(final ValidateLambdaFunctionConcurrencyRuleConfig parameters) {
        return parameters.getFunctionConcurrences().stream()
                .map(this::validateFunctionConcurrency)
                .collect(ImmutableList.toImmutableList());
    }

    private ValidationOutcome<LambdaReason> validateFunctionConcurrency(
            final ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency functionConcurrency) {
        final var optLambdaFunction = LambdaConnector.create().getLambdaFunction(functionConcurrency.functionName());

        if (optLambdaFunction.isEmpty()) {
            return ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND);
        }

        if (optLambdaFunction.get().concurrency().reservedConcurrentExecutions()
                != functionConcurrency.reservedConcurrency()) {
            return ValidationOutcome.invalid(LambdaReason.FUNCTION_CONCURRENCY_MISMATCH);
        }

        return ValidationOutcome.valid();
    }
}
