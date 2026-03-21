package com.lyr.cli.runners;

import com.lyr.config.RuleSet;
import com.lyr.report.ConsoleReporter;
import com.lyr.rule.Rule;
import com.lyr.rule.RuleFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScanAwsEnvironmentRunner {
    public static void run(final String userConfigFilePath) {
        if (userConfigFilePath != null) {
            RuleSet.loadUserRuleSet(userConfigFilePath);
        }

        final var rules = RuleSet.getInstance().getRuleToRuleConfigMap().keySet().stream()
                .map(RuleFactory::createRule)
                .toList();

        rules.forEach(Rule::evaluate);

        final var reporter = new ConsoleReporter();
        reporter.report(rules);
    }
}
