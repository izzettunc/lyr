package com.lyr.report.style;

import com.lyr.report.model.Finding;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface FindingStyler {

    String styleForConsole(List<Finding> findings);
}
