package com.lyr.rule.lambda.config;

import static com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.DISALLOWED_ARCHITECTURE_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanLambdaFunctionWithDisallowedArchitectureRuleConfigTest {

    @Test
    void testThatRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig = ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                .disallowedArchitecture("arm64")
                .build();
        final var expectedConfigMap = Map.of(DISALLOWED_ARCHITECTURE_CONFIG_KEY, "arm64");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }
}
