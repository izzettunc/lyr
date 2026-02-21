package com.example.report;

import com.example.rule.Rule;
import java.util.List;

public interface Reporter {
    void report(List<Rule> rules);
}
