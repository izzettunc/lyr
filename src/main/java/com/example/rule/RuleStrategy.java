package com.example.rule;

import com.example.rule.outcome.Outcome;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface RuleStrategy<T extends RuleConfig> {
    List<Outcome> execute(T parameters);
}
