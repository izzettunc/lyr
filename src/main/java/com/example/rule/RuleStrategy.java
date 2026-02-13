package com.example.rule;

import com.example.rule.outcome.Outcome;
import com.google.common.collect.ImmutableList;


public interface RuleStrategy<T extends RuleConfig> {
    ImmutableList<Outcome> execute(T parameters);
}
