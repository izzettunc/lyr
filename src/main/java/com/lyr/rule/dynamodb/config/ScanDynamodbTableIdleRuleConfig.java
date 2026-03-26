package com.lyr.rule.dynamodb.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@Getter
public class ScanDynamodbTableIdleRuleConfig implements RuleConfig {

    @NonNull
    private final Integer maxIdlePeriodInDays;

    @NonNull
    @Builder.Default
    private final Boolean excludeEmptyTables = Boolean.FALSE;

    public static ScanDynamodbTableIdleRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException(config.getClass().getSimpleName() + " is not a Map");
        }

        final var configMap = (Map<String, Object>) config;

        return ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays((Integer) RuleConfig.getMandatoryAttribute("maxIdlePeriodInDays", configMap))
                .excludeEmptyTables((Boolean) configMap.getOrDefault("excludeEmptyTables", Boolean.FALSE))
                .build();
    }
}
