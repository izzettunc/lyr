package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.TracingMode;

@Slf4j
public class ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig ignored) {
        final var listOfLambdaFunctionConfigurations = LambdaConnector.create().listLambdaFunctionConfigurations();
        final var findings = listOfLambdaFunctionConfigurations.stream()
                .filter(functionConfiguration -> !TracingMode.ACTIVE.equals(
                        functionConfiguration.tracingConfig().mode()))
                .map(FunctionConfiguration::functionName)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(listOfLambdaFunctionConfigurations.size())
                .log("Found {} function(s) with problems out of {} function(s).");

        return findings;
    }
}
