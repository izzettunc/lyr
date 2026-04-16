package com.lyr.rule.lambda.config;

import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;

import com.lyr.exception.rule.config.BadRuleConfigException;
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
public class ValidateLambdaFunctionTriggerStateRuleConfig implements RuleConfig {

    public static final String ENABLED = "enabled";
    public static final String DISABLED = "disabled";

    public record LambdaFunctionTriggerState(String functionName, boolean enabled) {}

    private List<LambdaFunctionTriggerState> functionTriggerStates;

    public static ValidateLambdaFunctionTriggerStateRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE, "map");
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
            throw new BadRuleConfigException("State attribute of " + VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE
                    + " rule config, must be either enabled or disabled");
        }

        return ENABLED.equalsIgnoreCase(state);
    }
}
