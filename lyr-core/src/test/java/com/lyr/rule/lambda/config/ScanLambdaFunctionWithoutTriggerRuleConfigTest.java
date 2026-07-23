package com.lyr.rule.lambda.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanLambdaFunctionWithoutTriggerRuleConfigTest {

    @Test
    void testThatRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig =
                ScanLambdaFunctionWithoutTriggerRuleConfig.builder().build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig =
                ScanLambdaFunctionWithoutTriggerRuleConfig.builder().build();
        final var expectedConfigMap = Map.of();

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatRuleConfigValidationPassesGivenValidRuleConfig() {
        // Given
        final var ruleConfig =
                ScanLambdaFunctionWithoutTriggerRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(ruleConfig::validate);
    }
}
