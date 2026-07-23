package com.lyr.rule.lambda.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@Builder
@Value
@JsonDeserialize(
        builder = ScanLambdaFunctionWithoutTriggerRuleConfig.ScanLambdaFunctionWithoutTriggerRuleConfigBuilder.class)
public class ScanLambdaFunctionWithoutTriggerRuleConfig implements RuleConfig {

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of();
    }

    @Override
    public ScanLambdaFunctionWithoutTriggerRuleConfig copy() {
        return ScanLambdaFunctionWithoutTriggerRuleConfig.builder().build();
    }

    @Override
    public void validate() {}

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithoutTriggerRuleConfigBuilder {}
}
