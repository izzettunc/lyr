package com.lyr.rule.cloudwatch.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigTest {

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigIsParsedSuccessfully() {
        // Given
        final Object configList = List.of(15);
        final Object configMap = Map.of("abc", 15, "def", 30);

        final var expectedRuleConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        // When
        final var actualRuleConfigList = ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.parse(configList);
        final var actualRuleConfigMap = ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.parse(configMap);
        final var actualRuleConfigNull = ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.parse(null);

        // Then
        assertThat(actualRuleConfigList).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
        assertThat(actualRuleConfigMap).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
        assertThat(actualRuleConfigNull).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
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
