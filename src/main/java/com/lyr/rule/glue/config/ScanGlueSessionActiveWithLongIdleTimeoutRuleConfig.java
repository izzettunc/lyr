package com.lyr.rule.glue.config;

import static com.lyr.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

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

    @NonNull
    private final Integer maxIdleTimeoutInMinutes;

    public static ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, "map");
        }

        final var configMap = (Map<String, Integer>) config;

        final var scanGlueSessionActiveWithLongIdleTimeoutRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(RuleConfig.getMandatoryAttribute("maxIdleTimeoutInMinutes", configMap))
                        .build();

        if (scanGlueSessionActiveWithLongIdleTimeoutRuleConfig.getMaxIdleTimeoutInMinutes() < 0) {
            throw new BadRuleConfigException(
                    "Max idle timeout in minutes attribute of " + SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT
                            + " rule config, must be equal or greater than 0");
        }

        return scanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
    }
}
