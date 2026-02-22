package com.example.config;

import com.example.report.ReportType;
import com.example.rule.RuleConfig;
import com.example.rule.RuleConfigFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

@Getter
public class Config {
    private static final String DEFAULT_CONFIG_PATH = "defaultConfig.yaml";
    private static final Config DEFAULT_CONFIG = new Config(DEFAULT_CONFIG_PATH, true);
    private static final Config CONFIG = new Config();

    private Map<String, RuleConfig> ruleConfig = new HashMap<>();
    private final ReportType reportType = ReportType.CONSOLE;
    private String path;
    private boolean isDefault;

    private Config() {}

    public Config(final String configPath) {
        this(configPath, false);
    }

    private Config(final String configPath, final boolean isDefaultConfig) {
        this.path = configPath;
        this.isDefault = isDefaultConfig;
        load();
    }

    public void load() {
        final Yaml yaml = new Yaml();
        try (InputStream configInputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            final Map<String, Object> rawConfig = yaml.load(configInputStream);
            ruleConfig = generateRuleConfigFromRawConfig(rawConfig);
        } catch (final Exception exception) {
            throw new RuntimeException("Failed to load defaultConfig.yaml", exception);
        }
    }

    private Map<String, RuleConfig> generateRuleConfigFromRawConfig(final Map<String, Object> rawConfig) {
        final Map<String, RuleConfig> ruleConfigMap = new HashMap<>();
        for (final Map.Entry<String, Object> entry : rawConfig.entrySet()) {
            if (entry.getValue() != null) {
                ruleConfigMap.put(entry.getKey(), RuleConfigFactory.createRuleConfig(entry.getKey(), entry.getValue()));
            } else if (isDefault) {
                ruleConfigMap.put(entry.getKey(), null);
            } else {
                final var defaultRuleConfig = DEFAULT_CONFIG.getRuleConfig().get(entry.getKey());
                if (defaultRuleConfig != null || DEFAULT_CONFIG.getRuleConfig().containsKey(entry.getKey())) {
                    ruleConfigMap.put(entry.getKey(), defaultRuleConfig);
                } else {
                    throw new IllegalArgumentException("No config found for rule: " + entry.getKey());
                }
            }
        }
        return ruleConfigMap;
    }

    public static void loadUserConfig(final String path) {
        CONFIG.path = path;
        CONFIG.load();
    }

    public static Config getConfig() {
        return CONFIG;
    }
}
