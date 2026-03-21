package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.RuleStrategy;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.services.lambda.LambdaConnector;
import java.util.List;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;

public class ValidateLambdaFunctionTriggerStateRuleImpl
        implements RuleStrategy<ValidateLambdaFunctionTriggerStateRuleConfig> {
    private static final String ENABLED = "enabled";

    @Override
    public ImmutableList<Outcome> execute(final ValidateLambdaFunctionTriggerStateRuleConfig parameters) {
        return parameters.getFunctionTriggerStates().stream()
                .map(this::validateLambdaFunctionTriggerState)
                .collect(ImmutableList.toImmutableList());
    }

    private ValidationOutcome<LambdaReason> validateLambdaFunctionTriggerState(
            final ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState lambdaFunctionTriggerState) {
        final var lambdaConnector = LambdaConnector.create();
        final var optLambdaFunction = lambdaConnector.getLambdaFunction(lambdaFunctionTriggerState.functionName());

        if (optLambdaFunction.isEmpty()) {
            return ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND);
        }

        final List<EventSourceMappingConfiguration> listOfEventMappings;
        try {
            listOfEventMappings = lambdaConnector.listEventSourceMappings(lambdaFunctionTriggerState.functionName());
        } catch (final ResourceNotFoundException resourceNotFoundException) {
            return ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND);
        }

        if (listOfEventMappings.isEmpty() && lambdaFunctionTriggerState.enabled()) {
            return ValidationOutcome.invalid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS);
        } else if (listOfEventMappings.isEmpty()) {
            return ValidationOutcome.valid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS);
        }

        final var allDisabled =
                listOfEventMappings.stream().noneMatch(mapping -> ENABLED.equalsIgnoreCase(mapping.state()));
        final var allEnabled =
                listOfEventMappings.stream().allMatch(mapping -> ENABLED.equalsIgnoreCase(mapping.state()));

        return validateEventMappings(lambdaFunctionTriggerState, allEnabled, allDisabled);
    }

    private ValidationOutcome<LambdaReason> validateEventMappings(
            final ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState lambdaFunctionTriggerState,
            final boolean allEnabled,
            final boolean allDisabled) {

        if (allEnabled && lambdaFunctionTriggerState.enabled()) {
            // Wanted to be enabled and all enabled
            return ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED);
        } else if (allEnabled) {
            // Wanted to be disabled but all enabled
            return ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED);
        } else if (allDisabled && !lambdaFunctionTriggerState.enabled()) {
            // Wanted to be disabled and all disabled
            return ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED);
        } else if (allDisabled) {
            // Wanted to be enabled but all disabled
            return ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED);
        } else if (lambdaFunctionTriggerState.enabled()) {
            // Wanted to be enabled but mixed outcome
            return ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED);
        } else {
            // Wanted to be disabled but mixed outcome
            return ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_DISABLED);
        }
    }
}
