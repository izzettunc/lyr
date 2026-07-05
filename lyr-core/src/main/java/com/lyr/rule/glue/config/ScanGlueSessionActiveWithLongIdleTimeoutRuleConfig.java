package com.lyr.rule.glue.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig implements RuleConfig {

    public static final String MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY = "maxIdleTimeoutInMinutes";
    public static final int DEFAULT_MAX_IDLE_TIMEOUT_IN_MINUTES = 15;

    @Builder.Default
    @NonNull
    Integer maxIdleTimeoutInMinutes = DEFAULT_MAX_IDLE_TIMEOUT_IN_MINUTES;

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY, maxIdleTimeoutInMinutes.toString());
    }

    @Override
    public ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig copy() {
        return ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeoutInMinutes)
                .build();
    }
}
