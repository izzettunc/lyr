package com.lyr.rule.lambda.config;

import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;

import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.rule.RuleConfig;
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

    public static ValidateLambdaFunctionConcurrencyRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY, "map");
        }

        final var lambdaFunctionConcurrences = ((Map<String, Integer>) config)
                .entrySet().stream()
                        .map(entry -> new LambdaFunctionConcurrency(entry.getKey(), entry.getValue()))
                        .toList();

        return ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(lambdaFunctionConcurrences)
                .build();
    }
}
