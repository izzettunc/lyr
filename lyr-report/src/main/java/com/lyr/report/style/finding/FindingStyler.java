package com.lyr.report.style.finding;

import com.lyr.report.model.Finding;
import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface FindingStyler {

    String styleForPlainText(List<Finding> findings);
}
