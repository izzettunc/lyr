package com.lyr.cli.commands;

import static picocli.CommandLine.Option;

import com.lyr.cli.runners.ScanAwsEnvironmentRunner;
import com.lyr.cli.util.LyrLogLevel;
import com.lyr.cli.util.VersionProvider;
import com.lyr.config.Settings;
import com.lyr.report.ReportType;
import com.lyr.services.ServiceProvider;
import com.lyr.util.log.LogUtil;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;

@Slf4j
@Command(
        name = "scan-env",
        mixinStandardHelpOptions = true,
        description = "Scans relative cloud environments using the relative ruleset",
        versionProvider = VersionProvider.class)
public class ScanEnvironmentCommand {

    @Command(
            name = "aws",
            description = "Scans you aws environment using relative rulest and credentials",
            mixinStandardHelpOptions = true,
            versionProvider = VersionProvider.class)
    void aws(
            @Option(
                            names = {"-c", "--config"},
                            description = "Path to user rule set config file that specifies the ruleset")
                    final Optional<String> optionalUserRuleSetConfigFilePath,
            @Option(
                            names = {"-r", "--reportType"},
                            description = "Path to user config file that specifies the ruleset")
                    final Optional<ReportType> optionalReportType,
            @Option(
                            names = {"-p", "--profile"},
                            description = "AWS profile that defines desired credential or configuration to use")
                    final Optional<String> optionalProfile,
            @Option(
                            names = {"-l", "--logLevel"},
                            description = "Desired level of details for logs")
                    final Optional<LyrLogLevel> optionalLogLevel) {

        final var settingsBuilder = Settings.builder();
        settingsBuilder.userRuleSetConfigPath(optionalUserRuleSetConfigFilePath);

        optionalReportType.ifPresent(settingsBuilder::reportType);
        optionalProfile.ifPresent(settingsBuilder::profile);
        optionalLogLevel.ifPresent(lyrLogLevel -> settingsBuilder.logLevel(lyrLogLevel.asLogbackLogLevel()));

        final var settings = settingsBuilder.build();
        Settings.setAppSettings(settings);

        LogUtil.setLogLevelAtRoot(Settings.getAppSettings().getLogLevel());
        ServiceProvider.configure(Settings.getAppSettings().getProfile());

        log.atInfo()
                .setMessage(
                        "Starting to scan aws environment. User rule set config path: {}, Report type: {}, Log level: {}.")
                .addArgument(() -> LogUtil.optionalToString(optionalUserRuleSetConfigFilePath))
                .addArgument(() -> LogUtil.optionalToString(optionalReportType, settings.getReportType()))
                .addArgument(() -> LogUtil.optionalToString(optionalLogLevel, settings.getLogLevel()))
                .log();

        ScanAwsEnvironmentRunner.run();

        log.atInfo()
                .setMessage(
                        "Finished scanning aws environment. User rule set config path: {}, Report type: {}, Log level: {}.")
                .addArgument(() -> LogUtil.optionalToString(optionalUserRuleSetConfigFilePath))
                .addArgument(() -> LogUtil.optionalToString(optionalReportType, settings.getReportType()))
                .addArgument(() -> LogUtil.optionalToString(optionalLogLevel, settings.getLogLevel()))
                .log();
    }
}
