package com.lyr.report.plain.text;

import com.lyr.report.Reporter;
import com.lyr.report.io.OutputStrategy;
import com.lyr.report.model.Execution;
import com.lyr.report.style.finding.StylerFactory;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import com.lyr.report.style.plain.text.TitleLevel;
import com.lyr.util.RuleDefinition;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlainTextReporter implements Reporter {
    private final PlainTextReportStyler styler;
    private final OutputStrategy outputStrategy;

    @Override
    public void report(final List<Execution> executions) {
        outputStrategy.write(styler.buildTitleBlock(TitleLevel.PRIMARY, "LYR REPORT"));

        for (final Execution execution : executions) {
            final var findingStyler = StylerFactory.getStylerFor(RuleDefinition.definitionByName(execution.name()));

            outputStrategy.write(styler.buildTitleBlock(TitleLevel.SECONDARY, "Report for " + execution.name()));
            outputStrategy.write(styler.styleExecutionConfiguration(execution.configuration()));
            outputStrategy.write(findingStyler.styleForPlainText(execution.findings()));
            outputStrategy.write("");
        }
    }
}
