package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.TracingMode;

public class ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig ignored) {
        return LambdaConnector.create().listLambdaFunctionConfigurations().stream()
                .filter(functionConfiguration -> !TracingMode.ACTIVE.equals(
                        functionConfiguration.tracingConfig().mode()))
                .map(FunctionConfiguration::functionName)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());
    }
}
