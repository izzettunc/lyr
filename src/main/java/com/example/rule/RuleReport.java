package com.example.rule;

import com.example.rule.outcome.Outcome;
import com.google.common.collect.ImmutableList;

public interface RuleReport<T extends RuleConfig> {

    String report(T ruleConfig, ImmutableList<? extends Outcome> outcome);
}
