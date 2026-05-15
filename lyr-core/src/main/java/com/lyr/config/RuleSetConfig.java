package com.lyr.config;

import com.lyr.config.parser.RuleSetConfigParser;
import com.lyr.exception.config.NotYetInitializedException;
import com.lyr.exception.config.RuleWithNoConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.util.RuleDefinition;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class RuleSetConfig {
    private static RuleSetConfig configInstance;

    private final Map<RuleDefinition, RuleConfig> ruleDefinitionToRuleConfigMap = new EnumMap<>(RuleDefinition.class);

    public RuleSetConfig(final Map<RuleDefinition, RuleConfig> definitionToConfigMap) {
        ruleDefinitionToRuleConfigMap.putAll(definitionToConfigMap);
    }

    public RuleConfig getConfig(final RuleDefinition ruleDefinition) {
        if (!ruleDefinitionToRuleConfigMap.containsKey(ruleDefinition)) {
            throw new RuleWithNoConfigException("No config found for rule: " + ruleDefinition.getRuleName());
        }

        return ruleDefinitionToRuleConfigMap.get(ruleDefinition).copy();
    }

    public Set<RuleDefinition> getAllAvailableRuleDefinition() {
        return Set.copyOf(ruleDefinitionToRuleConfigMap.keySet());
    }

    public static RuleSetConfig loadUserRuleSetConfig(final String path) {
        setRuleSetConfig(new RuleSetConfig(RuleSetConfigParser.parseRuleSetConfig(path)));
        return getRuleSetConfig();
    }

    public static RuleSetConfig loadDefaultRuleSetConfig() {
        setRuleSetConfig(new RuleSetConfig(RuleSetConfigParser.parseDefaultRuleSetConfig()));
        return getRuleSetConfig();
    }

    public static void setRuleSetConfig(final RuleSetConfig userRuleSetConfig) {
        configInstance = userRuleSetConfig;
    }

    public static RuleSetConfig getRuleSetConfig() {
        if (configInstance == null) {
            throw new NotYetInitializedException(
                    "Rule set configuration can not be accessed as it is not yet initialized.");
        }

        return configInstance;
    }
}
