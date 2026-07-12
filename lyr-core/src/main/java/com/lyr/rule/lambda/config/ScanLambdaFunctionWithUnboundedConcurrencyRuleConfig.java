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
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig
                        .ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigBuilder.class)
public class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig implements RuleConfig {

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of();
    }

    @Override
    public ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig copy() {
        return ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigBuilder {}
}
