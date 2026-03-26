package com.lyr.rule;

import com.lyr.rule.outcome.Outcome;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface RuleStrategy<T extends RuleConfig> {
    List<Outcome> execute(T parameters);
}
