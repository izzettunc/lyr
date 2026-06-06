package com.lyr.rule;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface RuleExecutionStrategy<T extends RuleConfig> {
    ImmutableList<Finding> execute(T parameters);
}
