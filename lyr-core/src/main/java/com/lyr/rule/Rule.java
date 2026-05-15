package com.lyr.rule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lyr.report.model.Execution;
import com.lyr.report.model.Finding;
import com.lyr.util.RuleDefinition;
import lombok.Getter;

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
        final ImmutableList<Finding> findings = ruleExecutionStrategy.execute(ruleConfig);
        final ImmutableMap<String, String> ruleConfigAsStringMap =
                ruleConfig != null ? ImmutableMap.copyOf(ruleConfig.getConfigAsStringMap()) : ImmutableMap.of();
        execution = Execution.builder()
                .name(ruleDefinition.getRuleName())
                .code(ruleDefinition.getRuleCode())
                .configuration(ruleConfigAsStringMap)
                .findings(findings)
                .build();
        return execution;
    }
}
