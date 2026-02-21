package com.example.rule.lambda.config;

import com.example.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ValidateLambdaFunctionConcurrencyRuleConfig implements RuleConfig {

    public record LambdaFunctionConcurrency(String functionName, int reservedConcurrency) {}

    private List<LambdaFunctionConcurrency> functionConcurrences;

    public static ValidateLambdaFunctionConcurrencyRuleConfig parse(Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException("Invalid config type for ValidateLambdaFunctionConcurrencyRuleConfig");
        }

        var lambdaFunctionConcurrences = ((Map<String, Integer>) config)
                .entrySet().stream()
                        .map(entry -> new LambdaFunctionConcurrency(entry.getKey(), entry.getValue()))
                        .toList();

        return ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(lambdaFunctionConcurrences)
                .build();
    }
}
