package com.example.report;

import com.example.rule.Rule;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Reporter {
    void report(List<Rule> rules);
}
