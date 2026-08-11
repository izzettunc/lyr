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
                ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig
                        .ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfigBuilder.class)
public class ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig implements RuleConfig {

    @Override
    public Map<String, Object> getConfigAsMap() {
        return Map.of();
    }

    @Override
    public ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig copy() {
        return ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig.builder().build();
    }

    @Override
    public void validate() {}

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfigBuilder {}
}
