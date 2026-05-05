package com.lyr.rule;

import com.lyr.exception.rule.config.MissingMandatoryRuleConfigAttributeException;
import java.util.Map;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface RuleConfig {
    static <T> T getMandatoryAttribute(final String key, final Map<String, T> map) {
        if (!map.containsKey(key)) {
            throw new MissingMandatoryRuleConfigAttributeException(key + " is mandatory config attribute");
        }

        return map.get(key);
    }

    Map<String, String> getConfigAsStringMap();

    RuleConfig copy();
}
