package com.lyr.cli;

import com.lyr.cli.commands.ScanEnvironmentCommand;
import com.lyr.cli.util.VersionProvider;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "lyr",
        mixinStandardHelpOptions = true,
        description =
                "Scans provided cloud environments or IaC templates using the pre or user defined ruleset for waste, security, best practices, etc.",
        subcommands = ScanEnvironmentCommand.class,
        versionProvider = VersionProvider.class)
public class Lyr {
    static void main(final String... args) {
        final int exitCode = new CommandLine(new Lyr()).execute(args);
        System.exit(exitCode);
    }
}
