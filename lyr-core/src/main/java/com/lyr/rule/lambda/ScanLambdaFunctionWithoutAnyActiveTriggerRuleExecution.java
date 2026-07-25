package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import com.lyr.util.StringUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

@Slf4j
public class ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig ruleConfig) {
        final var lambdaConnector = LambdaConnector.create();
        final var listOfLambdaFunctionConfigurations = lambdaConnector.listLambdaFunctionConfigurations();
        final var findings = listOfLambdaFunctionConfigurations.stream()
                .map(FunctionConfiguration::functionName)
                .filter(functionName -> isEmptyOrAllConsideredAsNotActive(
                        lambdaConnector.listEventSourceMappings(functionName),
                        ruleConfig.getTriggerStatesConsideredAsActive()))
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(listOfLambdaFunctionConfigurations.size())
                .log("Found {} function(s) with problems out of {} function(s).");

        return findings;
    }

    private boolean isEmptyOrAllConsideredAsNotActive(
            final List<EventSourceMappingConfiguration> listOfEventSourceMapping,
            final List<String> statesConsideredAsActive) {
        return listOfEventSourceMapping.stream()
                .map(mapping -> !StringUtil.containsIgnoreCase(statesConsideredAsActive, mapping.state()))
                .reduce(Boolean::logicalAnd)
                .orElse(true);
    }
}
