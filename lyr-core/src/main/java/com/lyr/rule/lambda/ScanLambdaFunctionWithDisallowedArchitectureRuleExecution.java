package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

@Slf4j
public class ScanLambdaFunctionWithDisallowedArchitectureRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithDisallowedArchitectureRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithDisallowedArchitectureRuleConfig config) {
        final var listOfLambdaFunctionConfigurations = LambdaConnector.create().listLambdaFunctionConfigurations();
        final var findings = listOfLambdaFunctionConfigurations.stream()
                .filter(functionConfig -> functionConfig
                        .architecturesAsStrings()
                        .contains(config.getDisallowedArchitecture().toLowerCase(Locale.ROOT)))
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
