package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.services.lambda.LambdaConnector;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ignored) {
        return LambdaConnector.create().listLambdaFunctionNames().stream()
                .map(functionName -> LambdaConnector.create().getLambdaFunction(functionName))
                .filter(optLambdaFunction ->
                        optLambdaFunction.isPresent() && optLambdaFunction.get().concurrency() == null)
                .map(optLambdaFunction ->
                        optLambdaFunction.get().configuration().functionName())
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());
    }
}
