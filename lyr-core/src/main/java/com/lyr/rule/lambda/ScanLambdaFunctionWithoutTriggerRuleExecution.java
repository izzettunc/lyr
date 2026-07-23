package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithoutTriggerRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

@Slf4j
public class ScanLambdaFunctionWithoutTriggerRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithoutTriggerRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithoutTriggerRuleConfig ignored) {
        final var lambdaConnector = LambdaConnector.create();
        final var listOfLambdaFunctionConfigurations = lambdaConnector.listLambdaFunctionConfigurations();
        final var findings = listOfLambdaFunctionConfigurations.stream()
                .map(FunctionConfiguration::functionName)
                .filter(functionName ->
                        lambdaConnector.listEventSourceMappings(functionName).isEmpty())
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(listOfLambdaFunctionConfigurations.size())
                .log("Found {} function(s) with problems out of {} function(s).");

        return findings;
    }
}
