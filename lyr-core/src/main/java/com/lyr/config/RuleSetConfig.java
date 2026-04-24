package com.lyr.config;

import com.lyr.exception.config.RuleSetConfigLoadException;
import com.lyr.exception.config.RuleWithNoConfigException;
import com.lyr.rule.FileUtils;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.RuleConfigFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

@Getter
public final class RuleSetConfig {
    private static final String DEFAULT_RULESET_CONFIG_PATH = "defaultRuleSet.yaml";
    private static final RuleSetConfig DEFAULT_RULE_SET_CONFIG = new RuleSetConfig(DEFAULT_RULESET_CONFIG_PATH, true);

    private static final RuleSetConfig INSTANCE = new RuleSetConfig();

    private Map<String, RuleConfig> ruleToRuleConfigMap = new HashMap<>();
    private boolean isDefault;

    private RuleSetConfig() {}

    private RuleSetConfig(final String ruleSetPath) {
        this(ruleSetPath, false);
    }

    private RuleSetConfig(final String ruleSetPath, final boolean isDefaultRuleSet) {
        this.isDefault = isDefaultRuleSet;
        if (this.isDefault) {
            loadFromResource(ruleSetPath);
        } else {
            loadFromSystem(ruleSetPath);
        }
    }

    public void loadFromResource(final String path) {
        try (InputStream ruleSetInputStream = FileUtils.getInputStreamFromResource(path)) {
            final Yaml yaml = new Yaml();
            final Map<String, Object> rawConfig = yaml.load(ruleSetInputStream);
            ruleToRuleConfigMap = generateRuleToRuleConfigFromRawRuleSetMap(rawConfig);
        } catch (final Exception exception) {
            throw new RuleSetConfigLoadException("Failed to load ruleset from resource. Path: " + path, exception);
        }
    }

    public void loadFromSystem(final String path) {
        try (InputStream ruleSetInputStream = FileUtils.getInputStreamFromSystem(path)) {
            final Yaml yaml = new Yaml();
            final Map<String, Object> rawConfig = yaml.load(ruleSetInputStream);
            ruleToRuleConfigMap = generateRuleToRuleConfigFromRawRuleSetMap(rawConfig);
        } catch (final Exception exception) {
            throw new RuleSetConfigLoadException("Failed to load ruleset from system. Path: " + path, exception);
        }
    }

    private Map<String, RuleConfig> generateRuleToRuleConfigFromRawRuleSetMap(final Map<String, Object> rawRuleSetMap) {
        final Map<String, RuleConfig> ruleConfigMap = new HashMap<>();

        if (rawRuleSetMap == null) {
            return ruleConfigMap;
        }

        for (final Map.Entry<String, Object> ruleNameToRuleConfigEntry : rawRuleSetMap.entrySet()) {
            if (ruleNameToRuleConfigEntry.getValue() != null) {
                ruleConfigMap.put(
                        ruleNameToRuleConfigEntry.getKey(),
                        RuleConfigFactory.createRuleConfig(
                                ruleNameToRuleConfigEntry.getKey(), ruleNameToRuleConfigEntry.getValue()));
            } else if (isDefault) {
                ruleConfigMap.put(ruleNameToRuleConfigEntry.getKey(), null);
            } else {
                final var defaultRuleConfig =
                        DEFAULT_RULE_SET_CONFIG.getRuleToRuleConfigMap().get(ruleNameToRuleConfigEntry.getKey());
                if (defaultRuleConfig != null
                        || DEFAULT_RULE_SET_CONFIG
                                .getRuleToRuleConfigMap()
                                .containsKey(ruleNameToRuleConfigEntry.getKey())) {
                    ruleConfigMap.put(ruleNameToRuleConfigEntry.getKey(), defaultRuleConfig);
                } else {
                    throw new RuleWithNoConfigException(
                            "No config found for rule: " + ruleNameToRuleConfigEntry.getKey());
                }
            }
        }
        return ruleConfigMap;
    }

    public static void loadUserRuleSetConfig(final String path) {
        INSTANCE.loadFromSystem(path);
    }

    public static RuleSetConfig getInstance() {
        if (INSTANCE.ruleToRuleConfigMap.isEmpty()) {
            return DEFAULT_RULE_SET_CONFIG;
        } else {
            return INSTANCE;
        }
    }
}
