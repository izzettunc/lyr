package com.lyr.rule;

import com.google.common.collect.ImmutableList;
import com.lyr.report.ReportType;
import com.lyr.rule.outcome.Outcome;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class Rule {
    protected final String ruleName;

    protected final RuleConfig ruleConfig;
    protected final RuleStrategy ruleExecutionStrategy;
    protected final RuleReport ruleReportStrategy;

    protected List<Outcome> outcome;

    Rule(final String name, final RuleConfig config, final RuleStrategy ruleStrategy, final RuleReport ruleReport) {
        this.ruleName = name;
        this.ruleConfig = config;
        this.ruleExecutionStrategy = ruleStrategy;
        this.ruleReportStrategy = ruleReport;
    }

    public abstract String report(ReportType reportType);

    public abstract List<? extends Outcome> evaluate();

    public abstract List<? extends Outcome> reevaluate();

    public static <I, O> List<O> recastOutcomeList(final List<I> outcomes) {
        return outcomes.stream().map(outcome -> (O) outcome).collect(ImmutableList.toImmutableList());
    }
}
