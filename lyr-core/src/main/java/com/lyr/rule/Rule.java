package com.lyr.rule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lyr.report.model.Execution;
import com.lyr.report.model.Finding;
import com.lyr.util.RuleDefinition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Rule {
    protected final RuleDefinition ruleDefinition;

    protected final RuleConfig ruleConfig;
    protected final RuleExecutionStrategy ruleExecutionStrategy;

    protected Execution execution;

    Rule(final RuleDefinition definition, final RuleConfig config, final RuleExecutionStrategy executionStrategy) {
        this.ruleDefinition = definition;
        this.ruleConfig = config;
        this.ruleExecutionStrategy = executionStrategy;
    }

    public Execution evaluate() {
        if (execution == null) {
            return reevaluate();
        }

        return execution;
    }

    public Execution reevaluate() {
        log.atInfo()
                .setMessage("Started to execute {} rule.")
                .addArgument(ruleDefinition::getRuleName)
                .log();

        final ImmutableList<Finding> findings = ruleExecutionStrategy.execute(ruleConfig);
        execution = buildExecutionFromFindings(findings);

        log.atInfo()
                .setMessage("Finished executing {} rule.")
                .addArgument(ruleDefinition::getRuleName)
                .log();

        return execution;
    }

    private Execution buildExecutionFromFindings(final ImmutableList<Finding> findings) {
        final ImmutableMap<String, Object> ruleConfigAsStringMap =
                ruleConfig != null ? ImmutableMap.copyOf(ruleConfig.getConfigAsMap()) : ImmutableMap.of();
        return Execution.builder()
                .name(ruleDefinition.getRuleName())
                .code(ruleDefinition.getRuleCode())
                .configuration(ruleConfigAsStringMap)
                .findings(findings)
                .build();
    }
}
