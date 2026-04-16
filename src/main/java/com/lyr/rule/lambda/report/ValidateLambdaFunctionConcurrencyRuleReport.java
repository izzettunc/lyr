package com.lyr.rule.lambda.report;

import com.lyr.report.console.ConsoleReportStyler;
import com.lyr.rule.RuleReport;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import java.util.List;

public class ValidateLambdaFunctionConcurrencyRuleReport
        implements RuleReport<ValidateLambdaFunctionConcurrencyRuleConfig> {

    @Override
    public String reportToConsole(
            final ValidateLambdaFunctionConcurrencyRuleConfig ruleConfig, final List<? extends Outcome> outcomes) {
        final var validationOutcomes = (List<ValidationOutcome<LambdaReason>>) outcomes;

        final StringBuilder reportBuilder = new StringBuilder();
        for (int i = 0; i < outcomes.size(); i++) {
            final var functionName = ruleConfig.getFunctionConcurrences().get(i).functionName();
            final var outcome = validationOutcomes.get(i);

            final var outcomeReport = outcome.success()
                    ? String.format("Lambda function '%s' has expected concurrency.", functionName)
                    : String.format("Lambda function '%s' validation failed: %s", functionName, outcome.reason());

            final var styledOutcomeReport = ConsoleReportStyler.styleOutcome(outcomeReport, outcome.success());
            reportBuilder.append(ConsoleReportStyler.toNewLine(styledOutcomeReport));
        }

        return reportBuilder.toString();
    }
}
