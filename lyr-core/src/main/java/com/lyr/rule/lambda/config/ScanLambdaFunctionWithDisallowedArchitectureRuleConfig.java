package com.lyr.rule.lambda.config;

import com.lyr.rule.RuleConfig;
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
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig
                        .ScanLambdaFunctionWithDisallowedArchitectureRuleConfigBuilder.class)
public class ScanLambdaFunctionWithDisallowedArchitectureRuleConfig implements RuleConfig {
    public static final String DISALLOWED_ARCHITECTURE_CONFIG_KEY = "disallowedArchitecture";

    public static final String DEFAULT_DISALLOWED_ARCHITECTURE = "x86_64";

    @Builder.Default
    @NonNull
    String disallowedArchitecture = DEFAULT_DISALLOWED_ARCHITECTURE;

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of(DISALLOWED_ARCHITECTURE_CONFIG_KEY, disallowedArchitecture);
    }

    @Override
    public ScanLambdaFunctionWithDisallowedArchitectureRuleConfig copy() {
        return ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                .disallowedArchitecture(disallowedArchitecture)
                .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithDisallowedArchitectureRuleConfigBuilder {}
}
