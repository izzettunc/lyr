package com.example.rule.lambda.config;

import com.example.rule.RuleConfig;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
