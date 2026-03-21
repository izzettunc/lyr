package com.lyr.rule;

import java.util.Map;

public interface RuleConfig {
    static <T> T getMandatoryAttribute(final String key, final Map<String, T> map) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException(key + " is mandatory config attribute");
        }

        return map.get(key);
    }
}
