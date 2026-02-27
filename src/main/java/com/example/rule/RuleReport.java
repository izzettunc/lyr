package com.example.rule;

import com.example.rule.outcome.Outcome;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface RuleReport<T extends RuleConfig> {

    String report(T ruleConfig, List<? extends Outcome> outcome);
}
