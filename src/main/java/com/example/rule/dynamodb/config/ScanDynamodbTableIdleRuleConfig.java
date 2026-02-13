package com.example.rule.dynamodb.config;

import com.example.rule.RuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class ScanDynamodbTableIdleRuleConfig implements RuleConfig {
    public static final String NAME = "scan.dynamodb.table.idle";

    @NonNull
    private final Integer maxIdlePeriodInDays;
    @NonNull
    private final Boolean excludeEmptyTables;

    public static ScanDynamodbTableIdleRuleConfig parse(Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException(config.getClass().getSimpleName() + " is not a Map");
        }

        var configMap = (Map<String, Object>) config;

        return ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays((Integer) configMap.get("maxIdlePeriodInDays"))
                .excludeEmptyTables((Boolean) configMap.get("excludeEmptyTables"))
                .build();

    }
}
