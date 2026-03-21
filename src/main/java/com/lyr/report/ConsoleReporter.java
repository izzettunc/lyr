package com.lyr.report;

import com.lyr.rule.Rule;
import java.util.List;

public class ConsoleReporter implements Reporter {

    @Override
    public void report(final List<Rule> rules) {
        final var block = "##########################";

        System.out.println(block);
        System.out.println("##  LYR REPORT   ##");
        System.out.println(block);

        for (final Rule rule : rules) {
            System.out.print(rule.report());
        }
    }
}
