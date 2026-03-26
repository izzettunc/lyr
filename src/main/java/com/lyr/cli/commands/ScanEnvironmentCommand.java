package com.lyr.cli.commands;

import static picocli.CommandLine.Option;

import com.lyr.cli.runners.ScanAwsEnvironmentRunner;
import com.lyr.cli.util.VersionProvider;
import picocli.CommandLine.Command;

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
                            names = {"-c", "-config"},
                            description = "Path to user config file that specifies the ruleset")
                    final String userConfigFilePath) {
        ScanAwsEnvironmentRunner.run(userConfigFilePath);
    }
}
