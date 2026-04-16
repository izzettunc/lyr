package com.lyr.rule.lambda.report;

import static com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig.DISABLED;
import static com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig.ENABLED;

import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.rule.RuleReport;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import java.util.List;

public class ValidateLambdaFunctionTriggerStateRuleReport
        implements RuleReport<ValidateLambdaFunctionTriggerStateRuleConfig> {

    @Override
    public String reportToConsole(
            final ValidateLambdaFunctionTriggerStateRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {
        final var validationOutcomes = (List<ValidationOutcome<LambdaReason>>) outcomes;
        final StringBuilder reportBuilder = new StringBuilder();

        for (int i = 0; i < outcomes.size(); i++) {
            final var functionName =
                    ruleConfig.getFunctionTriggerStates().get(i).functionName();
            final var expectedState =
                    ruleConfig.getFunctionTriggerStates().get(i).enabled() ? ENABLED : DISABLED;
            final var outcome = validationOutcomes.get(i);

            final var outcomeReport = outcome.success()
                    ? String.format(
                            "Lambda function '%s' expected to be %s. Validation passed: %s",
                            functionName, expectedState, outcome.reason())
                    : String.format(
                            "Lambda function '%s' expected to be %s. Validation failed: %s",
                            functionName, expectedState, outcome.reason());

            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, outcome.success());
            reportBuilder.append(ConsoleReportStyler.toNewLine(styledOutcomeReport));
        }

        return reportBuilder.toString();
    }
}
