package com.lyr.rule.ssm.report;

import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.rule.RuleReport;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.rule.ssm.SsmReason;
import com.lyr.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import java.util.List;

public class ValidateSsmParameterExistsRuleReport implements RuleReport<ValidateSsmParameterExistsRuleConfig> {

    @Override
    public String reportToConsole(
            final ValidateSsmParameterExistsRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {
        final var validationOutcomes = (List<ValidationOutcome<SsmReason>>) outcomes;

        final StringBuilder reportBuilder = new StringBuilder();
        for (int i = 0; i < outcomes.size(); i++) {
            final var parameterName = ruleConfig.getParameterNames().get(i);
            final var outcome = validationOutcomes.get(i);

            final var outcomeReport = outcome.success()
                    ? String.format("Ssm parameter '%s' exists.", parameterName)
                    : String.format("Ssm parameter '%s' validation failed: %s", parameterName, outcome.reason());

            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, outcome.success());
            reportBuilder.append(ConsoleReportStyler.toNewLine(styledOutcomeReport));
        }

        return reportBuilder.toString();
    }
}
