package com.lyr.report.model;

import com.google.common.collect.ImmutableList;
import lombok.Builder;

@Builder
public record Report(String version, ImmutableList<Execution> executions) {

    public static Report copyOf(final Report report) {
        return Report.builder()
                .version(report.version())
                .executions(report.executions.stream().map(Execution::copyOf).collect(ImmutableList.toImmutableList()))
                .build();
    }
}
