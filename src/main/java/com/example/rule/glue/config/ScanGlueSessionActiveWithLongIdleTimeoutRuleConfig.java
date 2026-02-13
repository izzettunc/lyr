package com.example.rule.glue.config;

import com.example.rule.RuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig implements RuleConfig {
    public static final String NAME = "scan.glue.session.activeWithLongIdleTimeout";
    @NonNull
    private final Integer maxIdleTimeoutInMinutes;

    public static ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parse(Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException(config.getClass().getSimpleName() + " is not a Map");
        }

        var configMap = (Map<String, Object>) config;

        return ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes((Integer) configMap.get("maxIdleTimeoutInMinutes"))
                .build();

    }
}
