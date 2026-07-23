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
        builder =
                ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig
                        .ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfigBuilder.class)
public class ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig implements RuleConfig {

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of();
    }

    @Override
    public ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig copy() {
        return ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder().build();
    }

    @Override
    public void validate() {}

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfigBuilder {}
}
