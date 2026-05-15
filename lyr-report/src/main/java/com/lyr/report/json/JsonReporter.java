package com.lyr.report.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;
import com.lyr.report.Reporter;
import com.lyr.report.io.OutputStrategy;
import com.lyr.report.model.Execution;
import com.lyr.report.model.Report;
import java.util.List;
import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@AllArgsConstructor
public class JsonReporter implements Reporter {
    public static final String REPORT_VERSION = "0.0.0";

    private static final ObjectMapper JSON_OBJECT_MAPPER = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
            .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
            .build();

    private final OutputStrategy outputStrategy;

    @Override
    public void report(final List<Execution> executions) {
        final var report = Report.builder()
                .version(REPORT_VERSION)
                .executions(ImmutableList.copyOf(executions))
                .build();

        final var reportAsJsonString = JSON_OBJECT_MAPPER.writeValueAsString(report);

        outputStrategy.write(reportAsJsonString);
    }
}
