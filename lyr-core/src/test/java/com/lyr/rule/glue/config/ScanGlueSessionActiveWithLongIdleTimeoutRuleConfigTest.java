package com.lyr.rule.glue.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.exception.rule.config.MissingMandatoryRuleConfigAttributeException;
import com.lyr.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigTest {

    private static final String MAX_IDLE_TIMEOUT_IN_MINUTES = "maxIdleTimeoutInMinutes";

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsParsedCorrectly() {
        // Given
        final int maxIdleTimeoutInMinutes = 15;
        final Map<String, Integer> config = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES, maxIdleTimeoutInMinutes);
        final RuleConfig expectedRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeoutInMinutes)
                .build();

        // When
        final var actualRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigThrowsInvalidRuleConfigTypeExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final List<Integer> invalidConfig = List.of(15);

        // When & Then
        assertThatThrownBy(() -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(invalidConfig))
                .isInstanceOf(InvalidRuleConfigTypeException.class);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigThrowsMissingMandatoryRuleConfigAttributeExceptionWhenConfigHasMissingValues() {
        // Given
        final Map<String, Integer> configWithoutMandatoryAttributes = Map.of();

        // When & Then
        assertThatThrownBy(() ->
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(configWithoutMandatoryAttributes))
                .isInstanceOf(MissingMandatoryRuleConfigAttributeException.class);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigThrowsBadConfigExceptionWhenMaxIdleTimeoutInMinutesIsNegative() {
        // Given
        final Map<String, Integer> config = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES, -1);

        // When & Then
        assertThatThrownBy(() -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config))
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
}
