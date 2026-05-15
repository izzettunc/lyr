package com.lyr.report;

import com.lyr.report.model.Execution;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Reporter {
    void report(List<Execution> executions);
}
