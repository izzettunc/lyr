package com.lyr.rule;

import java.util.Map;
import java.util.function.Consumer;

public interface RuleConfig {
    static <T> void setConfigIfAttributePresent(final String key, final Map<String, ?> map, final Consumer<T> setter) {
        if (map.containsKey(key)) {
            setter.accept((T) map.get(key));
        }
    }

    Map<String, String> getConfigAsStringMap();

    RuleConfig copy();
}
