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

    @NonNull
    private final Integer maxIdlePeriodInDays;

    @NonNull
    @Builder.Default
    private final Boolean excludeEmptyTables = Boolean.FALSE;

    public static ScanDynamodbTableIdleRuleConfig parse(Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException(config.getClass().getSimpleName() + " is not a Map");
        }

        var configMap = (Map<String, Object>) config;

        return ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays((Integer) RuleConfig.getMandatoryAttribute("maxIdlePeriodInDays", configMap))
                .excludeEmptyTables((Boolean) configMap.getOrDefault("excludeEmptyTables", Boolean.FALSE))
                .build();

    }
}
