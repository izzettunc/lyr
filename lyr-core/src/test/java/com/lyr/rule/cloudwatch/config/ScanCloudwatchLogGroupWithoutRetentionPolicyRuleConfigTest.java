package com.lyr.rule.cloudwatch.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigTest {

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigIsCreatedSuccessfully() {
        // Given
        final var expectedRuleConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        // When
        final var actualRuleConfig = ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.create();

        // Then
        assertThat(actualRuleConfig).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
        final var expectedConfigMap = Map.of();

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }
}
