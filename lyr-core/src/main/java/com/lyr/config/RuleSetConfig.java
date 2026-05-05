package com.lyr.config;

import com.lyr.config.parser.RuleSetConfigParser;
import com.lyr.exception.config.NotYetInitializedException;
import com.lyr.exception.config.RuleWithNoConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.util.RuleDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RuleSetConfig {
    private static RuleSetConfig ruleSetConfig;

    private final Map<RuleDefinition, RuleConfig> ruleDefinitionToRuleConfigMap = new HashMap<>();

    public RuleSetConfig(final Map<RuleDefinition, RuleConfig> definitionToConfigMap) {
        ruleDefinitionToRuleConfigMap.putAll(definitionToConfigMap);
    }

    public RuleConfig getConfig(RuleDefinition ruleDefinition) {
        if (!ruleDefinitionToRuleConfigMap.containsKey(ruleDefinition)) {
            throw new RuleWithNoConfigException("No config found for rule: " + ruleDefinition.getRuleName());
        }

        return ruleDefinitionToRuleConfigMap.get(ruleDefinition).copy();
    }

    public Set<RuleDefinition> getAllAvailableRuleDefinition() {
        return Set.copyOf(ruleDefinitionToRuleConfigMap.keySet());
    }

    public static RuleSetConfig loadUserRuleSetConfig(final String path) {
        setUserRuleSetConfig(new RuleSetConfig(RuleSetConfigParser.parseRuleSetConfig(path)));
        return getUserRuleSetConfig();
    }

    public static RuleSetConfig loadUserRuleSetConfig() {
        setUserRuleSetConfig(new RuleSetConfig(RuleSetConfigParser.parseDefaultRuleSetConfig()));
        return getUserRuleSetConfig();
    }

    public static void setUserRuleSetConfig(final RuleSetConfig userRuleSetConfig) {
        ruleSetConfig = userRuleSetConfig;
    }

    public static RuleSetConfig getUserRuleSetConfig(){
        if (ruleSetConfig == null) {
            throw new NotYetInitializedException(
                    "Rule set configuration can not be accessed as it is not yet initialized.");
        }

        return ruleSetConfig;
    }

}
