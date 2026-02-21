package com.example.rule;

import java.util.Map;

public interface RuleConfig {
    static <T> T getMandatoryAttribute(String key, Map<String, T> map) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException(key + " is mandatory config attribute");
        }

        return map.get(key);
    }
}
