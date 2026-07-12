package com.lyr.rule.glue.config;

import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

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
@JsonDeserialize(
        builder =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig
                        .ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigBuilder.class)
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

    @Override
    public void validate() {
        if (maxIdleTimeoutInMinutes < 0) {
            throw BadRuleConfigException.forLessThanLimit(
                    MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY,
                    SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName(),
                    0);
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigBuilder {}
}
