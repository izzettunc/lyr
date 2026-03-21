package com.lyr.config;

import com.lyr.rule.RuleConfig;
import com.lyr.rule.RuleConfigFactory;
import com.lyr.util.FileUtils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

@Getter
public final class RuleSet {
    private static final String DEFAULT_RULESET_PATH = "defaultRuleSet.yaml";
    private static final RuleSet DEFAULT_RULE_SET = new RuleSet(DEFAULT_RULESET_PATH, true);

    private static final RuleSet INSTANCE = new RuleSet();

    private Map<String, RuleConfig> ruleToRuleConfigMap = new HashMap<>();
    private boolean isDefault;

    private RuleSet() {}

    private RuleSet(final String ruleSetPath) {
        this(ruleSetPath, false);
    }

    private RuleSet(final String ruleSetPath, final boolean isDefaultRuleSet) {
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
            throw new RuntimeException("Failed to load ruleset from resource. Path: " + path, exception);
        }
    }

    public void loadFromSystem(final String path) {
        try (InputStream ruleSetInputStream = FileUtils.getInputStreamFromSystem(path)) {
            final Yaml yaml = new Yaml();
            final Map<String, Object> rawConfig = yaml.load(ruleSetInputStream);
            ruleToRuleConfigMap = generateRuleToRuleConfigFromRawRuleSetMap(rawConfig);
        } catch (final Exception exception) {
            throw new RuntimeException("Failed to load ruleset from system. Path: " + path, exception);
        }
    }

    private Map<String, RuleConfig> generateRuleToRuleConfigFromRawRuleSetMap(final Map<String, Object> rawRuleSetMap) {
        final Map<String, RuleConfig> ruleConfigMap = new HashMap<>();
        for (final Map.Entry<String, Object> entry : rawRuleSetMap.entrySet()) {
            if (entry.getValue() != null) {
                ruleConfigMap.put(entry.getKey(), RuleConfigFactory.createRuleConfig(entry.getKey(), entry.getValue()));
            } else if (isDefault) {
                ruleConfigMap.put(entry.getKey(), null);
            } else {
                final var defaultRuleConfig =
                        DEFAULT_RULE_SET.getRuleToRuleConfigMap().get(entry.getKey());
                if (defaultRuleConfig != null
                        || DEFAULT_RULE_SET.getRuleToRuleConfigMap().containsKey(entry.getKey())) {
                    ruleConfigMap.put(entry.getKey(), defaultRuleConfig);
                } else {
                    throw new IllegalArgumentException("No config found for rule: " + entry.getKey());
                }
            }
        }
        return ruleConfigMap;
    }

    public static void loadUserRuleSet(final String path) {
        INSTANCE.loadFromSystem(path);
    }

    public static RuleSet getInstance() {
        return INSTANCE;
    }
}
