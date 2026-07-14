package com.lyr.rule;

import java.util.Map;

public interface RuleConfig {
    Map<String, String> getConfigAsStringMap();

    RuleConfig copy();

    void validate();
}
