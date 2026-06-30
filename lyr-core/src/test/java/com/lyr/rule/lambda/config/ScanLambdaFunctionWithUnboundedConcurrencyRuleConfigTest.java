package com.lyr.rule.lambda.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigTest {

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigIsParsedSuccessfully() {
        // Given
        final Object configList = List.of(15);
        final Object configMap = Map.of("abc", 15, "def", 30);

        final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig expectedRuleConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        // When
        final var actualRuleConfigList = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(configList);
        final var actualRuleConfigMap = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(configMap);
        final var actualRuleConfigNull = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(null);

        // Then
        assertThat(actualRuleConfigList).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
        assertThat(actualRuleConfigMap).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
        assertThat(actualRuleConfigNull).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        final var expectedConfigMap = Map.of();

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }
}
