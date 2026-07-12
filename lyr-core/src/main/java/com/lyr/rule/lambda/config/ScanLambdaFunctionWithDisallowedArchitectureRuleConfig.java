package com.lyr.rule.lambda.config;

import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.services.lambda.model.Architecture;
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

    public static final String DEFAULT_DISALLOWED_ARCHITECTURE =
            Architecture.X86_64.toString().toLowerCase(Locale.ROOT);

    private static final Set<String> POSSIBLE_ARCHITECTURES_SET = Set.of(
            Architecture.X86_64.toString().toLowerCase(Locale.ROOT),
            Architecture.ARM64.toString().toLowerCase(Locale.ROOT));

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

    @Override
    public void validate() {
        if (!POSSIBLE_ARCHITECTURES_SET.contains(disallowedArchitecture.toLowerCase(Locale.ROOT))) {
            throw BadRuleConfigException.forUnsupportedValues(
                    DISALLOWED_ARCHITECTURE_CONFIG_KEY,
                    SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE.getRuleName(),
                    POSSIBLE_ARCHITECTURES_SET);
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithDisallowedArchitectureRuleConfigBuilder {}
}
