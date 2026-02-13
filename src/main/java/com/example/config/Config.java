package com.example.config;

import com.example.report.ReportType;
import com.example.rule.RuleConfig;
import com.example.rule.RuleConfigFactory;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Config {
    public static final String DEFAULT_CONFIG_PATH = "defaultConfig.yaml";
    public static final Config DEFAULT_CONFIG = new Config(DEFAULT_CONFIG_PATH, true);
    public static final Config CONFIG = new Config();

    private Map<String, RuleConfig> ruleConfig = new HashMap<>();
    private ReportType reportType = ReportType.CONSOLE;
    private String path;
    private boolean isDefault = false;

    private Config() {

    }

    public Config(String path) {
        this(path, false);
    }

    private Config(String path, boolean isDefault) {
        this.path = path;
        this.isDefault = isDefault;
        load();
    }

    public void load() {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            Map<String, Object> rawConfig = yaml.load(in);
            ruleConfig = generateRuleConfigFromRawConfig(rawConfig);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load defaultConfig.yaml", e);
        }
    }

    private Map<String, RuleConfig> generateRuleConfigFromRawConfig(Map<String, Object> rawConfig) {
        Map<String, RuleConfig> ruleConfigMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : rawConfig.entrySet()) {
            if (entry.getValue() != null) {
                ruleConfigMap.put(entry.getKey(), RuleConfigFactory.createRuleConfig(entry.getKey(), entry.getValue()));
            } else if(isDefault) {
                ruleConfigMap.put(entry.getKey(), null);
            } else {
                var defaultRuleConfig = DEFAULT_CONFIG.getRuleConfig().get(entry.getKey());
                if (defaultRuleConfig != null || DEFAULT_CONFIG.getRuleConfig().containsKey(entry.getKey())) {
                    ruleConfigMap.put(entry.getKey(), defaultRuleConfig);
                } else {
                    throw new IllegalArgumentException("No config found for rule: " + entry.getKey());
                }
            }
        }
        return ruleConfigMap;
    }

    public static void loadUserConfig(String path) {
        CONFIG.path = path;
        CONFIG.load();
    }
}

