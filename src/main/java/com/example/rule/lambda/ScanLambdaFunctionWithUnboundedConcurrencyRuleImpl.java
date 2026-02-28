package com.example.rule.lambda;

import com.example.rule.RuleStrategy;
import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl
        implements RuleStrategy<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ignored) {
        return LambdaConnector.create().listLambdaFunctionNames().stream()
                .map(functionName -> LambdaConnector.create().getLambdaFunction(functionName))
                .filter(optLambdaFunction ->
                        optLambdaFunction.isPresent() && optLambdaFunction.get().concurrency() == null)
                .map(optLambdaFunction ->
                        optLambdaFunction.get().configuration().functionName())
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());
    }
}
