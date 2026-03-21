package com.lyr.rule.glue.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@Getter
public class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig implements RuleConfig {

    @NonNull
    private final Integer maxIdleTimeoutInMinutes;

    public static ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException(config.getClass().getSimpleName() + " is not a Map");
        }

        final var configMap = (Map<String, Integer>) config;

        return ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(RuleConfig.getMandatoryAttribute("maxIdleTimeoutInMinutes", configMap))
                .build();
    }
}
