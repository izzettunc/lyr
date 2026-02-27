package com.example.report;

import com.example.rule.Rule;
import java.util.List;

public class ConsoleReporter implements Reporter {

    @Override
    public void report(final List<Rule> rules) {
        final var block = "##########################";

        System.out.println(block);
        System.out.println("##  AWS DOCTOR REPORT   ##");
        System.out.println(block);

        for (final Rule rule : rules) {
            System.out.print(rule.report());
        }
    }
}
