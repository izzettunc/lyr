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
        Settings.getAppSettings()
                .getUserRuleSetConfigPath()
                .ifPresentOrElse(RuleSetConfig::loadUserRuleSetConfig, RuleSetConfig::loadDefaultRuleSetConfig);

        final var rules = RuleSetConfig.getRuleSetConfig().getAllAvailableRuleDefinition().stream()
                .map(RuleFactory::createRule)
                .toList();

        final var executions = rules.stream().map(Rule::evaluate).toList();

        final var reporter =
                ReporterFactory.createReporter(Settings.getAppSettings().getReportType());

        reporter.report(executions);
    }
}
