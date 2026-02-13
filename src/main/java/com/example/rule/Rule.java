package com.example.rule;

import com.example.rule.outcome.Outcome;
import com.google.common.collect.ImmutableList;
import lombok.Getter;


public abstract class Rule {
    @Getter
    protected final String name;

    @Getter
    protected final RuleConfig parameters;
    protected final RuleStrategy strategy;

    @Getter
    protected final RuleReport report;

    @Getter
    protected ImmutableList<Outcome> outcome;

    Rule(String name, RuleConfig parameters, RuleStrategy strategy, RuleReport report) {
        this.name = name;
        this.parameters = parameters;
        this.strategy = strategy;
        this.report = report;
    }

    public abstract String report();

    public abstract ImmutableList<? extends Outcome> evaluate();

    public abstract ImmutableList<? extends Outcome> reevaluate();

    public static <I, O> ImmutableList<O> recastOutcomeList(ImmutableList<I> outcomes) {
        return outcomes.stream().map(outcome -> (O) outcome).collect(ImmutableList.toImmutableList());
    }

}
