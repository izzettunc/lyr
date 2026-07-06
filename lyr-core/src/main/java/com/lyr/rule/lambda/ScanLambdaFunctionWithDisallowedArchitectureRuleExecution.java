package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import java.util.Locale;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

public class ScanLambdaFunctionWithDisallowedArchitectureRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithDisallowedArchitectureRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithDisallowedArchitectureRuleConfig config) {
        return LambdaConnector.create().listLambdaFunctionConfigurations().stream()
                .filter(functionConfig -> functionConfig
                        .architecturesAsStrings()
                        .contains(config.getDisallowedArchitecture().toLowerCase(Locale.ROOT)))
                .map(FunctionConfiguration::functionName)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());
    }
}
