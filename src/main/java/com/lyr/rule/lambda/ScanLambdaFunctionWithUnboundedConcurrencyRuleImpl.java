package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.RuleStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import com.lyr.services.lambda.LambdaConnector;

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
