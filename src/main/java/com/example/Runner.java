package com.example;

import com.example.config.Config;
import com.example.report.ConsoleReporter;
import com.example.rule.Rule;
import com.example.rule.RuleFactory;

public class Runner {
    public static void main(String[] args) {
        Config.loadUserConfig("userConfig.yaml");

        var rules = Config.getConfig()
                .getRuleConfig()
                .keySet()
                .stream()
                .map(RuleFactory::createRule)
                .toList();

        rules.forEach(Rule::evaluate);

        var reporter = new ConsoleReporter();
        reporter.report(rules);
    }
}
