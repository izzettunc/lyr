package com.example.rule.lambda.config;

import com.example.rule.RuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class ValidateLambdaFunctionTriggerStateRuleConfig implements RuleConfig {

    public record LambdaFunctionTriggerState(String functionName, boolean enabled) {
    }

    private List<LambdaFunctionTriggerState> functionTriggerStates;

    public static ValidateLambdaFunctionTriggerStateRuleConfig parse(Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException("Invalid config type for ValidateLambdaFunctionTriggerStateConfig ");
        }

        var lambdaFunctionTriggerStates = ((Map<String, String>) config)
                .entrySet()
                .stream()
                .map(entry ->
                        new LambdaFunctionTriggerState(entry.getKey(), parseStateToBoolean(entry.getValue())))
                .toList();

        return ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(lambdaFunctionTriggerStates)
                .build();
    }

    private static boolean parseStateToBoolean(String state){
        if (!state.equalsIgnoreCase("enabled") && !state.equalsIgnoreCase("disabled")) {
            throw new IllegalArgumentException("Invalid state for the function");
        }

        return state.equalsIgnoreCase("enabled");
    }
}
