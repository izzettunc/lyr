package com.example.rule.lambda;

import com.example.rule.RuleStrategy;
import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;

public class ValidateLambdaFunctionTriggerStateRuleImpl implements RuleStrategy<ValidateLambdaFunctionTriggerStateRuleConfig> {
    private static final String ENABLED = "enabled";

    @Override
    public ImmutableList<Outcome> execute(ValidateLambdaFunctionTriggerStateRuleConfig parameters) {

        return parameters.getFunctionTriggerStates().stream()
                .map(lambdaFunctionTriggerState -> {
                    var optLambdaFunction = LambdaConnector.getInstance().getLambdaFunction(lambdaFunctionTriggerState.functionName());

                    if (optLambdaFunction.isEmpty()) {
                        return ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND);
                    }

                    var optEventMappings = LambdaConnector.getInstance().listEventSourceMappings(lambdaFunctionTriggerState.functionName());

                    if (optEventMappings.isEmpty() && lambdaFunctionTriggerState.enabled()) {
                        return ValidationOutcome.invalid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS);
                    } else if (optEventMappings.isEmpty()) {
                        return ValidationOutcome.valid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS);
                    }

                    var allDisabled = optEventMappings.get().eventSourceMappings().stream()
                            .noneMatch(mapping -> mapping.state().equalsIgnoreCase(ENABLED));

                    if (allDisabled && lambdaFunctionTriggerState.enabled()) {
                        return ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED);
                    } else if (allDisabled) {
                        return ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED);
                    } else if (!lambdaFunctionTriggerState.enabled()) {
                        return ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_DISABLED);
                    } else {
                        return ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED);
                    }

                })
                .collect(ImmutableList.toImmutableList());

    }
}
