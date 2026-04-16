package com.lyr.cli.runners;

import com.lyr.config.RuleSetConfig;
import com.lyr.config.Settings;
import com.lyr.report.ReporterFactory;
import com.lyr.rule.Rule;
import com.lyr.rule.RuleFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScanAwsEnvironmentRunner {
    public static void run() {
        Settings.getAppSettings().getUserRuleSetConfigPath().ifPresent(RuleSetConfig::loadUserRuleSetConfig);

        final var rules = RuleSetConfig.getInstance().getRuleToRuleConfigMap().keySet().stream()
                .map(RuleFactory::createRule)
                .toList();

        rules.forEach(Rule::evaluate);

        final var reporter =
                ReporterFactory.createReporter(Settings.getAppSettings().getReportType());
        reporter.report(rules);
    }
}
