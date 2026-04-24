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

    @NonNull
    private final Integer maxIdlePeriodInDays;

    @NonNull
    @Builder.Default
    private final Boolean excludeEmptyTables = Boolean.FALSE;

    public static ScanDynamodbTableIdleRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(SCAN_DYNAMODB_TABLE_IDLE.getRuleName(), "map");
        }

        final var configMap = (Map<String, Object>) config;

        final var scanDynamodbTableIdleRuleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays((Integer) RuleConfig.getMandatoryAttribute("maxIdlePeriodInDays", configMap))
                .excludeEmptyTables((Boolean) configMap.getOrDefault("excludeEmptyTables", Boolean.FALSE))
                .build();

        if (scanDynamodbTableIdleRuleConfig.getMaxIdlePeriodInDays() < 1) {
            throw new BadRuleConfigException("Max idle period attribute of " + SCAN_DYNAMODB_TABLE_IDLE.getRuleName()
                    + " rule config, must be longer than a day");
        }

        return scanDynamodbTableIdleRuleConfig;
    }
}
