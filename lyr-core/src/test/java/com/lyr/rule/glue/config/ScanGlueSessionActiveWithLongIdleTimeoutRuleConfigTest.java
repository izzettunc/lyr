package com.lyr.rule.glue.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigTest {

    private static final String MAX_IDLE_TIMEOUT_IN_MINUTES = "maxIdleTimeoutInMinutes";

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsParsedCorrectly() {
        // Given
        final int maxIdleTimeoutInMinutes = 123;
        final Map<String, Integer> config = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES, maxIdleTimeoutInMinutes);
        final RuleConfig expectedRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeoutInMinutes)
                .build();

        // When
        final var actualRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.create(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreatesDefaultOneWhenParsingFails() {
        // Given
        final RuleConfig expectedRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();

        final List<Integer> invalidConfig = List.of(123);

        // When
        final var actualRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.create(invalidConfig);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsParsedCorrectlyWithoutAnyAttribute() {
        // Given
        final Map<String, Object> configWithoutAttributes = Map.of();
        final RuleConfig expectedRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();

        // When
        final var actualRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.create(configWithoutAttributes);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigThrowsBadConfigExceptionWhenMaxIdleTimeoutInMinutesIsNegative() {
        // Given
        final Map<String, Integer> config = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES, -1);

        // When & Then
        assertThatThrownBy(() -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.create(config))
                .isInstanceOf(BadRuleConfigException.class);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsCopiedCorrectly() {
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
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(123)
                .build();
        final var expectedConfigMap = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES, "123");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigHasDefaultValues() {
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
