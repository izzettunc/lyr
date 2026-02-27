package com.example.rule;

import com.example.rule.outcome.ValidationOutcome;
import java.util.List;

public class ValidationRule<T extends Enum<T>> extends Rule {

    ValidationRule(
            final String name, final RuleConfig parameters, final RuleStrategy strategy, final RuleReport report) {
        super(name, parameters, strategy, report);
    }

    @Override
    public String report() {
        if (outcome == null) {
            throw new IllegalStateException(
                    "Rule outcome is not evaluated yet. Please call evaluate() before report().");
        }

        return ruleReportStrategy.report(ruleConfig, outcome);
    }

    @Override
    public List<ValidationOutcome<T>> evaluate() {
        if (outcome == null) {
            outcome = ruleExecutionStrategy.execute(ruleConfig);
        }

        return Rule.recastOutcomeList(outcome);
    }

    @Override
    public List<ValidationOutcome<T>> reevaluate() {
        outcome = ruleExecutionStrategy.execute(ruleConfig);
        return Rule.recastOutcomeList(outcome);
    }
}
