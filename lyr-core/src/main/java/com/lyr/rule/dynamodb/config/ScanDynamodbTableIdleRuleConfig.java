package com.lyr.rule.dynamodb.config;

import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@Builder
@Value
@JsonDeserialize(builder = ScanDynamodbTableIdleRuleConfig.ScanDynamodbTableIdleRuleConfigBuilder.class)
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
    public Map<String, Object> getConfigAsMap() {
        return Map.of(
                MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, maxIdlePeriodInDays,
                EXCLUDE_EMPTY_TABLES_CONFIG_KEY, excludeEmptyTables);
    }

    @Override
    public ScanDynamodbTableIdleRuleConfig copy() {
        return ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(maxIdlePeriodInDays)
                .excludeEmptyTables(excludeEmptyTables)
                .build();
    }

    @Override
    public void validate() {
        if (maxIdlePeriodInDays < 1) {
            throw BadRuleConfigException.forLessThanLimit(
                    MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, SCAN_DYNAMODB_TABLE_IDLE.getRuleName(), 1);
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanDynamodbTableIdleRuleConfigBuilder {}
}
