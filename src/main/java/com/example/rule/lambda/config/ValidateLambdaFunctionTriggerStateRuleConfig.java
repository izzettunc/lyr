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
public class ValidateLambdaFunctionTriggerStateRuleConfig implements RuleConfig {

    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    public record LambdaFunctionTriggerState(String functionName, boolean enabled) {}

    private List<LambdaFunctionTriggerState> functionTriggerStates;

    public static ValidateLambdaFunctionTriggerStateRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException("Invalid config type for ValidateLambdaFunctionTriggerStateConfig ");
        }

        final var lambdaFunctionTriggerStates = ((Map<String, String>) config)
                .entrySet().stream()
                        .map(entry ->
                                new LambdaFunctionTriggerState(entry.getKey(), parseStateToBoolean(entry.getValue())))
                        .toList();

        return ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(lambdaFunctionTriggerStates)
                .build();
    }

    private static boolean parseStateToBoolean(final String state) {
        if (!ENABLED.equalsIgnoreCase(state) && !DISABLED.equalsIgnoreCase(state)) {
            throw new IllegalArgumentException("Invalid state for the function");
        }

        return ENABLED.equalsIgnoreCase(state);
    }
}
