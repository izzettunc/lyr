package com.lyr.report.model;

import lombok.Builder;

@Builder
public record Finding(String identifier, String reason) {
    public static Finding byId(final String identifier) {
        return Finding.builder().identifier(identifier).build();
    }

    public static Finding copyOf(final Finding finding) {
        return Finding.builder()
                .identifier(finding.identifier)
                .reason(finding.reason())
                .build();
    }
}
