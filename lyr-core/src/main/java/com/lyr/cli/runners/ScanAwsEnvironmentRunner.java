package com.lyr.cli.runners;

import com.lyr.config.RuleSetConfig;
import com.lyr.config.Settings;
import com.lyr.report.ReporterFactory;
import com.lyr.rule.Rule;
import com.lyr.rule.RuleFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ScanAwsEnvironmentRunner {
    public static void run() {
        Settings.getAppSettings()
                .getUserRuleSetConfigPath()
                .ifPresentOrElse(RuleSetConfig::loadUserRuleSetConfig, RuleSetConfig::loadDefaultRuleSetConfig);

        log.info("Started to build rules using rule set config.");
        final var rules = RuleSetConfig.getRuleSetConfig().getAllAvailableRuleDefinition().stream()
                .map(RuleFactory::createRule)
                .toList();
        log.info("Finished building rules using rule set config.");

        log.info("Started to execute rules.");
        final var executions = rules.stream().map(Rule::evaluate).toList();
        log.info("Finished executing rules.");

        log.info("Started to prepare the report.");
        final var reporter =
                ReporterFactory.createReporter(Settings.getAppSettings().getReportType());

        reporter.report(executions);
        log.info("Finished preparing the report.");
    }
}
