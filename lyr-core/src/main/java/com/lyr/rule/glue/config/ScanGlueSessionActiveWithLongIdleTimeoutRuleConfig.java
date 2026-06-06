package com.lyr.rule.glue.config;

import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

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
public class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig implements RuleConfig {

    public static final String MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY = "maxIdleTimeoutInMinutes";

    @NonNull
    private final Integer maxIdleTimeoutInMinutes;

    public static ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(
                    SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName(), "map");
        }

        final var configMap = (Map<String, Integer>) config;

        final var scanGlueSessionActiveWithLongIdleTimeoutRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(
                                RuleConfig.getMandatoryAttribute(MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY, configMap))
                        .build();

        if (scanGlueSessionActiveWithLongIdleTimeoutRuleConfig.getMaxIdleTimeoutInMinutes() < 0) {
            throw new BadRuleConfigException("Max idle timeout in minutes attribute of "
                    + SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName()
                    + " rule config, must be equal or greater than 0");
        }

        return scanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
    }

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
