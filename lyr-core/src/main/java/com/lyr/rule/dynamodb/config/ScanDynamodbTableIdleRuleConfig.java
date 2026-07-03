package com.lyr.rule.dynamodb.config;

import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
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
    public static final String EXCLUDE_EMPTY_TABLES_CONFIG_KEY = "excludeEmptyTables";
    public static final String MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY = "maxIdlePeriodInDays";

    @Builder.Default
    @NonNull
    private final Integer maxIdlePeriodInDays = 30;

    @NonNull
    @Builder.Default
    private final Boolean excludeEmptyTables = Boolean.FALSE;

    public static ScanDynamodbTableIdleRuleConfig create(final Object config) {
        ScanDynamodbTableIdleRuleConfig ruleConfig;

        try {
            ruleConfig = parse(config);
        } catch (final Exception exception) {
            ruleConfig = ScanDynamodbTableIdleRuleConfig.builder().build();
        }

        validate(ruleConfig);

        return ruleConfig;
    }

    private static ScanDynamodbTableIdleRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(SCAN_DYNAMODB_TABLE_IDLE.getRuleName(), "map");
        }

        final var configMap = (Map<String, Object>) config;

        final var ruleConfigBuilder = ScanDynamodbTableIdleRuleConfig.builder();
        RuleConfig.setConfigIfAttributePresent(
                MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, configMap, ruleConfigBuilder::maxIdlePeriodInDays);
        RuleConfig.setConfigIfAttributePresent(
                EXCLUDE_EMPTY_TABLES_CONFIG_KEY, configMap, ruleConfigBuilder::excludeEmptyTables);

        return ruleConfigBuilder.build();
    }

    private static void validate(final ScanDynamodbTableIdleRuleConfig ruleConfig) {
        if (ruleConfig.getMaxIdlePeriodInDays() < 1) {
            throw new BadRuleConfigException("Max idle period attribute of " + SCAN_DYNAMODB_TABLE_IDLE.getRuleName()
                    + " rule config, must be longer than a day");
        }
    }

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
