package com.example.report;

import com.example.rule.Rule;
import java.util.List;

public class ConsoleReporter implements Reporter {

    @Override
    public void report(List<Rule> rules) {
        System.out.println("##########################");
        System.out.println("##  AWS DOCTOR REPORT   ##");
        System.out.println("##########################");
        for (Rule rule : rules) {
            System.out.print(rule.report());
        }
    }
}
