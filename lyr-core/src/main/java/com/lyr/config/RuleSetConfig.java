package com.lyr.config;

import com.lyr.config.parser.RuleSetConfigParser;
import com.lyr.config.parser.RuleSet;
import com.lyr.config.parser.RuleSetParser;
import com.lyr.exception.NotYetInitializedException;
import com.lyr.exception.config.RuleWithNoConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.util.RuleDefinition;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public static RuleSetConfig from(final RuleSet ruleSet) {
        final var definitionToConfigMap = Arrays.stream(RuleDefinition.values())
                .filter(ruleDefinition -> ruleSet.getRuleConfig(ruleDefinition) != null)
                .collect(Collectors.toMap(Function.identity(), ruleSet::getRuleConfig));

        return new RuleSetConfig(definitionToConfigMap);
    }

    public static RuleSetConfig loadUserRuleSetConfig(final String path) {
        log.info("Started to load user rule set config. Path: {}.", path);
        final var ruleSet = RuleSetParser.parseRuleSet(path);
        setRuleSetConfig(from(ruleSet));
        final var ruleSetConfig = getRuleSetConfig();
        log.info("Finished loading user rule set config. Path: {}.", path);
        return ruleSetConfig;
    }

    public static RuleSetConfig loadDefaultRuleSetConfig() {
        log.info("Started to load default rule set config.");
        final var defaultRuleSet = RuleSetParser.parseDefaultRuleSet();
        setRuleSetConfig(from(defaultRuleSet));
        final var ruleSetConfig = getRuleSetConfig();
        log.info("Finished loading default rule set config.");
        return ruleSetConfig;
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
