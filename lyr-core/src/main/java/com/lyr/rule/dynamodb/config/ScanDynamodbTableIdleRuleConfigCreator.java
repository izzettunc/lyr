package com.lyr.rule.dynamodb.config;

import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.EXCLUDE_EMPTY_TABLES_CONFIG_KEY;
import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.RuleConfigCreator;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScanDynamodbTableIdleRuleConfigCreator extends RuleConfigCreator<ScanDynamodbTableIdleRuleConfig> {

    @Override
    protected Optional<ScanDynamodbTableIdleRuleConfig> parse(final Object input) {
        if (!(input instanceof Map)) {
            log.atWarn()
                    .addArgument(SCAN_DYNAMODB_TABLE_IDLE::getRuleName)
                    .log("Provided config for {} rule is not a map. Alternating to default config for this rule.");
            return Optional.empty();
        }

        final var configMap = (Map<String, Object>) input;

        final var ruleConfigBuilder = ScanDynamodbTableIdleRuleConfig.builder();
        RuleConfig.setConfigIfAttributePresent(
                MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, configMap, ruleConfigBuilder::maxIdlePeriodInDays);
        RuleConfig.setConfigIfAttributePresent(
                EXCLUDE_EMPTY_TABLES_CONFIG_KEY, configMap, ruleConfigBuilder::excludeEmptyTables);

        return Optional.of(ruleConfigBuilder.build());
    }

    @Override
    protected ScanDynamodbTableIdleRuleConfig createDefaultConfig() {
        return ScanDynamodbTableIdleRuleConfig.builder().build();
    }

    @Override
    protected void validate(final ScanDynamodbTableIdleRuleConfig ruleConfig) {
        if (ruleConfig == null) {
            throw new BadRuleConfigException("Rule config must not be null");
        }

        if (ruleConfig.getMaxIdlePeriodInDays() < 1) {
            throw new BadRuleConfigException("Max idle period attribute of " + SCAN_DYNAMODB_TABLE_IDLE.getRuleName()
                    + " rule config, must be longer than a day");
        }
    }
}
