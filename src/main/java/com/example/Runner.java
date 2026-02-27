package com.example;

import com.example.config.Config;
import com.example.report.ConsoleReporter;
import com.example.rule.Rule;
import com.example.rule.RuleFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Runner {
    static void main() {
        Config.loadUserConfig("userConfig.yaml");

        final var rules = Config.getInstance().getRuleConfig().keySet().stream()
                .map(RuleFactory::createRule)
                .toList();

        rules.forEach(Rule::evaluate);

        final var reporter = new ConsoleReporter();
        reporter.report(rules);
    }
}
