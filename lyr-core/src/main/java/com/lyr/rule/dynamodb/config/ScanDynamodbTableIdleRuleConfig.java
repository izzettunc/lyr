package com.lyr.rule.dynamodb.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class ScanDynamodbTableIdleRuleConfig implements RuleConfig {
    public static final String EXCLUDE_EMPTY_TABLES_CONFIG_KEY = "excludeEmptyTables";
    public static final String MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY = "maxIdlePeriodInDays";

    public static final Integer DEFAULT_MAX_IDLE_PERIOD_IN_DAYS = 30;
    public static final Boolean DEFAULT_EXCLUDE_EMPTY_TABLES = Boolean.FALSE;

    @Builder.Default
    @NonNull
    Integer maxIdlePeriodInDays = DEFAULT_MAX_IDLE_PERIOD_IN_DAYS;

    @NonNull
    @Builder.Default
    Boolean excludeEmptyTables = DEFAULT_EXCLUDE_EMPTY_TABLES;

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of(
                MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, maxIdlePeriodInDays.toString(),
                EXCLUDE_EMPTY_TABLES_CONFIG_KEY, excludeEmptyTables.toString());
    }

    @Override
    public ScanDynamodbTableIdleRuleConfig copy() {
        return ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(maxIdlePeriodInDays)
                .excludeEmptyTables(excludeEmptyTables)
                .build();
    }
}
