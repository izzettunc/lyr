package com.example.rule.lambda.config;

import com.example.rule.RuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class ValidateLambdaFunctionExistsRuleConfig implements RuleConfig {

    private List<String> functionNames;

    public static ValidateLambdaFunctionExistsRuleConfig parse(Object config) {
        if (!(config instanceof List)) {
            throw new IllegalArgumentException("Invalid config type for ValidateLambdaFunctionConcurrencyRuleConfig");
        }

        return ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames((List<String>) config)
                .build();
    }
}
