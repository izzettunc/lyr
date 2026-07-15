package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ignored) {
        final var listOfLambdaFunctionNames = LambdaConnector.create().listLambdaFunctionNames();
        final var findings = listOfLambdaFunctionNames.stream()
                .map(functionName -> LambdaConnector.create().getLambdaFunction(functionName))
                .filter(optLambdaFunction ->
                        optLambdaFunction.isPresent() && optLambdaFunction.get().concurrency() == null)
                .map(optLambdaFunction ->
                        optLambdaFunction.get().configuration().functionName())
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(listOfLambdaFunctionNames.size())
                .log("Found {} function(s) with problems out of {} function(s).");

        return findings;
    }
}
