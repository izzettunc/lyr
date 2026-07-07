package com.lyr.rule.lambda.config;

import static com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.DISALLOWED_ARCHITECTURE_CONFIG_KEY;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.RuleConfigCreator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator
        extends RuleConfigCreator<ScanLambdaFunctionWithDisallowedArchitectureRuleConfig> {
    private static final Set<String> POSSIBLE_ARCHITECTURES_SET = Set.of("x86_64", "arm64");

    @Override
    protected Optional<ScanLambdaFunctionWithDisallowedArchitectureRuleConfig> parse(final Object input) {
        if (!(input instanceof Map)) {
            log.atWarn()
                    .addArgument(SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE::getRuleName)
                    .log("Provided config for {} rule is not a map. Alternating to default config for this rule.");
            return Optional.empty();
        }

        try {
            final var configMap = (Map<String, String>) input;

            final var ruleConfigBuilder = ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder();

            RuleConfig.setConfigIfAttributePresent(
                    DISALLOWED_ARCHITECTURE_CONFIG_KEY, configMap, ruleConfigBuilder::disallowedArchitecture);

            return Optional.of(ruleConfigBuilder.build());
        } catch (final Exception exception) {
            log.atWarn()
                    .addArgument(SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE::getRuleName)
                    .addArgument(exception.getClass().getName())
                    .addArgument(exception.getMessage())
                    .log(
                            "Provided config for {} rule is can not be parsed due to an error. Alternating to default config for this rule. ErrorType: {}, Error: {}");
            return Optional.empty();
        }
    }

    @Override
    protected ScanLambdaFunctionWithDisallowedArchitectureRuleConfig createDefaultConfig() {
        return ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build();
    }

    @Override
    protected void validate(final ScanLambdaFunctionWithDisallowedArchitectureRuleConfig ruleConfig) {
        if (ruleConfig == null) {
            throw BadRuleConfigException.forNull(SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE.getRuleName());
        }

        if (!POSSIBLE_ARCHITECTURES_SET.contains(
                ruleConfig.getDisallowedArchitecture().toLowerCase(Locale.ROOT))) {
            throw BadRuleConfigException.forUnsupportedValues(
                    DISALLOWED_ARCHITECTURE_CONFIG_KEY,
                    SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE.getRuleName(),
                    POSSIBLE_ARCHITECTURES_SET);
        }
    }
}
