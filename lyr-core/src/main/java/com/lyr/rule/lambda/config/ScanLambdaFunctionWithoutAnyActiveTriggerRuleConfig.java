package com.lyr.rule.lambda.config;

import static com.lyr.services.lambda.EventSourceMappingState.ALL_TRIGGER_STATES_AS_STRING;
import static com.lyr.services.lambda.EventSourceMappingState.CREATING;
import static com.lyr.services.lambda.EventSourceMappingState.ENABLED;
import static com.lyr.services.lambda.EventSourceMappingState.ENABLING;
import static com.lyr.services.lambda.EventSourceMappingState.UPDATING;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.util.StringUtil;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@Builder
@Value
@JsonDeserialize(
        builder =
                ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig
                        .ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfigBuilder.class)
public class ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig implements RuleConfig {
    public static final String TRIGGER_STATES_CONSIDERED_AS_ACTIVE_KEY = "triggerStatesConsideredAsActive";

    public static final List<String> DEFAULT_TRIGGER_STATES_CONSIDERED_AS_ACTIVE =
            List.of(CREATING.getValue(), ENABLING.getValue(), ENABLED.getValue(), UPDATING.getValue());

    @Builder.Default
    @NonNull
    List<String> triggerStatesConsideredAsActive = DEFAULT_TRIGGER_STATES_CONSIDERED_AS_ACTIVE;

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of(
                TRIGGER_STATES_CONSIDERED_AS_ACTIVE_KEY,
                StringUtil.collectionToString(triggerStatesConsideredAsActive));
    }

    @Override
    public ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig copy() {
        return ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder()
                .triggerStatesConsideredAsActive(triggerStatesConsideredAsActive)
                .build();
    }

    @Override
    public void validate() {
        if (!ALL_TRIGGER_STATES_AS_STRING.containsAll(triggerStatesConsideredAsActive)) {
            throw BadRuleConfigException.forUnsupportedValues(
                    TRIGGER_STATES_CONSIDERED_AS_ACTIVE_KEY,
                    SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER.getRuleName(),
                    ALL_TRIGGER_STATES_AS_STRING);
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfigBuilder {}
}
