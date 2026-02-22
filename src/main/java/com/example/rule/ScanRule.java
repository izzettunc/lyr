package com.example.rule;

import com.example.rule.outcome.ScanOutcome;
import com.google.common.collect.ImmutableList;

public class ScanRule extends Rule {
    ScanRule(final String name, final RuleConfig parameters, final RuleStrategy strategy, final RuleReport report) {
        super(name, parameters, strategy, report);
    }

    @Override
    public String report() {
        if (outcome == null) {
            throw new IllegalStateException(
                    "Rule outcome is not evaluated yet. Please call evaluate() before report().");
        }

        return report.report(config, outcome);
    }

    @Override
    public ImmutableList<ScanOutcome> evaluate() {
        if (outcome == null) {
            outcome = strategy.execute(config);
        }
        return Rule.recastOutcomeList(outcome);
    }

    @Override
    public ImmutableList<ScanOutcome> reevaluate() {
        outcome = strategy.execute(config);
        return Rule.recastOutcomeList(outcome);
    }
}
