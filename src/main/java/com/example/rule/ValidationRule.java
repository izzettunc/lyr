package com.example.rule;

import com.example.rule.outcome.ValidationOutcome;
import com.google.common.collect.ImmutableList;

public class ValidationRule<T extends Enum<T>> extends Rule {

    ValidationRule(String name, RuleConfig parameters, RuleStrategy strategy, RuleReport report) {
        super(name, parameters, strategy, report);
    }

    @Override
    public String report() {
        if (outcome == null) {
            throw new IllegalStateException("Rule outcome is not evaluated yet. Please call evaluate() before report().");
        }

        return report.report(parameters, outcome);
    }

    @Override
    public ImmutableList<ValidationOutcome<T>> evaluate() {
        if (outcome == null) {
            outcome = strategy.execute(parameters);
        }

        return Rule.recastOutcomeList(outcome);
    }

    @Override
    public ImmutableList<ValidationOutcome<T>> reevaluate() {
        outcome = strategy.execute(parameters);
        return Rule.recastOutcomeList(outcome);
    }
}
