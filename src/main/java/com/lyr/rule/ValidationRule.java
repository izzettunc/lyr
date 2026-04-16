package com.lyr.rule;

import com.lyr.report.ReportType;
import com.lyr.rule.outcome.ValidationOutcome;
import java.util.List;

public class ValidationRule<T extends Enum<T>> extends Rule {

    ValidationRule(
            final String name, final RuleConfig parameters, final RuleStrategy strategy, final RuleReport report) {
        super(name, parameters, strategy, report);
    }

    @Override
    public String report(final ReportType reportType) {
        if (outcome == null) {
            throw new IllegalStateException(
                    "Rule outcome is not evaluated yet. Please call evaluate() before report().");
        }

        return switch (reportType) {
            case CONSOLE -> ruleReportStrategy.reportToConsole(ruleConfig, outcome);
        };
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
