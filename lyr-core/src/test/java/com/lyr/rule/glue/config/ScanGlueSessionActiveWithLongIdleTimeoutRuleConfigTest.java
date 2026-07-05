package com.lyr.rule.glue.config;

import static com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigTest {

    @Test
    void testThatRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(123)
                .build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(123)
                .build();
        final var expectedConfigMap = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY, "123");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatRuleConfigHasDefaultValues() {
        // Given
        final var expectedConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(15)
                .build();

        // When
        final var actualConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();

        // Then
        assertThat(actualConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
    }
}
