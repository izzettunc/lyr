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

    @Builder.Default
    @NonNull
    private final Integer maxIdleTimeoutInMinutes = 15;

    public static ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig create(final Object config) {
        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig;

        try {
            ruleConfig = parse(config);
        } catch (final Exception exception) {
            ruleConfig =
                    ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();
        }

        validate(ruleConfig);

        return ruleConfig;
    }

    private static ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(
                    SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName(), "map");
        }

        final var configMap = (Map<String, Integer>) config;

        final var ruleConfigBuilder = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder();
        RuleConfig.setConfigIfAttributePresent(
                MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY, configMap, ruleConfigBuilder::maxIdleTimeoutInMinutes);

        return ruleConfigBuilder.build();
    }

    private static void validate(final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig) {
        if (ruleConfig.getMaxIdleTimeoutInMinutes() < 0) {
            throw new BadRuleConfigException("Max idle timeout in minutes attribute of "
                    + SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName()
                    + " rule config, must be equal or greater than 0");
        }
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
