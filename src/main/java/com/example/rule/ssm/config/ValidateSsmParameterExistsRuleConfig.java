package com.example.rule.ssm.config;

import com.example.rule.RuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class ValidateSsmParameterExistsRuleConfig implements RuleConfig {

    private List<String> parameterNames;

    public static ValidateSsmParameterExistsRuleConfig parse(Object config) {
        if (!(config instanceof List)) {
            throw new IllegalArgumentException("Invalid config type for ValidateSsmParameterExistsRuleConfig");
        }

        return ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames((List<String>) config)
                .build();
    }
}
