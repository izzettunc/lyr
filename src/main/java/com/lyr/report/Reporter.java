package com.lyr.report;

import com.lyr.rule.Rule;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Reporter {
    void report(List<Rule> rules);
}
