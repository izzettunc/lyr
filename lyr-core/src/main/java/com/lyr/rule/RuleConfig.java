package com.lyr.rule;

import java.util.Map;

public interface RuleConfig {
    Map<String, Object> getConfigAsMap();

    RuleConfig copy();

    void validate();
}
