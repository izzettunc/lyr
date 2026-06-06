package com.lyr.report.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;

@Builder
public record Execution(
        String name, String code, ImmutableMap<String, String> configuration, ImmutableList<Finding> findings) {

    public static Execution copyOf(final Execution execution) {
        return Execution.builder()
                .name(execution.name())
                .code(execution.code())
                .configuration(ImmutableMap.copyOf(execution.configuration))
                .findings(execution.findings.stream().map(Finding::copyOf).collect(ImmutableList.toImmutableList()))
                .build();
    }
}
