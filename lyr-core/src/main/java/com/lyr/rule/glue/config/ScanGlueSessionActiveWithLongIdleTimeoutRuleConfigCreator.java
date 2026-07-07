package com.lyr.rule.glue.config;

import static com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.RuleConfigCreator;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator
        extends RuleConfigCreator<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    @Override
    protected Optional<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> parse(final Object input) {
        if (!(input instanceof Map)) {
            log.atWarn()
                    .addArgument(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT::getRuleName)
                    .log("Provided config for {} rule is not a map. Alternating to default config for this rule.");
            return Optional.empty();
        }

        try {
            final var configMap = (Map<String, Integer>) input;

            final var ruleConfigBuilder = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder();
            RuleConfig.setConfigIfAttributePresent(
                    MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY, configMap, ruleConfigBuilder::maxIdleTimeoutInMinutes);

            return Optional.of(ruleConfigBuilder.build());
        } catch (final Exception exception) {
            log.atWarn()
                    .addArgument(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT::getRuleName)
                    .addArgument(exception.getClass().getName())
                    .addArgument(exception.getMessage())
                    .log(
                            "Provided config for {} rule can not be parsed due to an error. Alternating to default config for this rule. ErrorType: {}, Error: {}");
            return Optional.empty();
        }
    }

    @Override
    protected ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig createDefaultConfig() {
        return ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();
    }

    @Override
    protected void validate(final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig) {
        if (ruleConfig == null) {
            throw BadRuleConfigException.forNull(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName());
        }

        if (ruleConfig.getMaxIdleTimeoutInMinutes() < 0) {
            throw BadRuleConfigException.forLessThanLimit(
                    MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY,
                    SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName(),
                    0);
        }
    }
}
