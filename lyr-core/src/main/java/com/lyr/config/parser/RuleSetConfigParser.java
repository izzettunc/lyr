package com.lyr.config.parser;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.exception.config.RuleSetConfigLoadException;
import com.lyr.util.exception.rule.UnknownRuleException;
import com.lyr.rule.FileUtils;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.RuleConfigFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.lyr.util.RuleDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuleSetConfigParser {
    private static final String DEFAULT_RULESET_CONFIG_PATH = "defaultRuleSet.yaml";
    private static Map<RuleDefinition, RuleConfig> DEFAULT_RULESET_CONFIG;
    private static boolean isDefaultLoaded;

    public static Map<RuleDefinition, RuleConfig> parseRuleSetConfig(String path) {
        if(!isDefaultLoaded) {
            loadDefaultRuleSet();
        }

        final var rawRuleSetConfigMap = parseRawRuleSetConfigFromSystem(path);
        return generateRuleToRuleConfigFromRawRuleSetMap(rawRuleSetConfigMap);
    }

    private static void loadDefaultRuleSet() {
        DEFAULT_RULESET_CONFIG = parseDefaultRuleSetConfig();
        isDefaultLoaded = true;
    }

    public static Map<RuleDefinition, RuleConfig> parseDefaultRuleSetConfig() {
        final var rawRuleSetConfigMap = parseRawRuleSetConfigFromResource(getDefaultRulesetConfigPath());
        return generateRuleToRuleConfigFromRawRuleSetMap(rawRuleSetConfigMap);
    }

    private static Map<String, Object> parseRawRuleSetConfigFromResource(final String path) {
        try (InputStream ruleSetInputStream = FileUtils.getInputStreamFromResource(path)) {
            return parseRawRuleSetConfig(ruleSetInputStream);
        } catch (final Exception exception) {
            throw new RuleSetConfigLoadException(String.format("Failed to load ruleset from resource. Exception: %s Path: %s", exception.getMessage(), path), exception);
        }
    }

    private static Map<String, Object> parseRawRuleSetConfigFromSystem(final String path) {
        try (InputStream ruleSetInputStream = FileUtils.getInputStreamFromSystem(path)) {
            return parseRawRuleSetConfig(ruleSetInputStream);
        } catch (final Exception exception) {
            throw new RuleSetConfigLoadException(String.format("Failed to load ruleset from system. Exception: %s Path: %s", exception.getMessage(), path), exception);
        }
    }

    private static Map<String, Object> parseRawRuleSetConfig(final InputStream inputStream) {
        final Yaml yaml = new Yaml();
        return yaml.load(inputStream);
    }

    private static Map<RuleDefinition, RuleConfig> generateRuleToRuleConfigFromRawRuleSetMap(final Map<String, Object> rawRuleSetMap) {
        final Map<RuleDefinition, RuleConfig> ruleConfigMap = new HashMap<>();

        if (rawRuleSetMap == null) {
            return ruleConfigMap;
        }

        for (final Map.Entry<String, Object> ruleNameToRuleConfigEntry : rawRuleSetMap.entrySet()) {
            RuleDefinition ruleDefinition;

            try {
                ruleDefinition = RuleDefinition.definitionByName(ruleNameToRuleConfigEntry.getKey());
            } catch (final UnknownRuleException unknownRuleException) {
                throw new RuleSetConfigLoadException(String.format("Failed to generate rule set config. Exception: %s", unknownRuleException.getMessage()), unknownRuleException);
            }

            RuleConfig ruleConfig;
            try {
                ruleConfig = RuleConfigFactory.createRuleConfig(ruleDefinition, ruleNameToRuleConfigEntry.getValue());
            } catch (final Exception exception) {
                if (!isDefaultLoaded) {
                    throw new RuleSetConfigLoadException(String.format("Failed to generate default rule set config. Exception: %s", exception.getMessage()), exception);
                } else if (!DEFAULT_RULESET_CONFIG.containsKey(ruleDefinition)) {
                    throw new RuleSetConfigLoadException(String.format("Failed to generate rule set config. Exception: %s", exception.getMessage()), exception);
                }

                // Opposite of above statement.
                // If default is loaded and default ruleset has the key, then pull it from the default
                ruleConfig = DEFAULT_RULESET_CONFIG.get(ruleDefinition);
            }

            ruleConfigMap.put(ruleDefinition, ruleConfig);
        }

        return ruleConfigMap;
    }

    @VisibleForTesting
    static String getDefaultRulesetConfigPath() {
        return DEFAULT_RULESET_CONFIG_PATH;
    }

    @VisibleForTesting
    static void setIsDefaultLoaded(final boolean isDefaultConfigLoaded) {
       RuleSetConfigParser.isDefaultLoaded = isDefaultConfigLoaded;
    }
}
