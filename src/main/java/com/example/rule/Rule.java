package com.example.rule;

import com.example.rule.outcome.Outcome;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

@Getter
public abstract class Rule {
    protected final String name;

    protected final RuleConfig config;
    protected final RuleStrategy strategy;
    protected final RuleReport report;

    protected ImmutableList<Outcome> outcome;

    Rule(
            final String ruleName,
            final RuleConfig ruleConfig,
            final RuleStrategy ruleStrategy,
            final RuleReport ruleReport) {
        this.name = ruleName;
        this.config = ruleConfig;
        this.strategy = ruleStrategy;
        this.report = ruleReport;
    }

    public abstract String report();

    public abstract ImmutableList<? extends Outcome> evaluate();

    public abstract ImmutableList<? extends Outcome> reevaluate();

    public static <I, O> ImmutableList<O> recastOutcomeList(final ImmutableList<I> outcomes) {
        return outcomes.stream().map(outcome -> (O) outcome).collect(ImmutableList.toImmutableList());
    }
}
