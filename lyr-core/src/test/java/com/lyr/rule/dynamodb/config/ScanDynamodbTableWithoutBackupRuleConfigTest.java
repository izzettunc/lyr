package com.lyr.rule.dynamodb.config;

import static com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutBackupRuleConfig.PASS_IF_BACKUP_PLAN_ENABLED_CONFIG_KEY;
import static com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutBackupRuleConfig.PASS_IF_PITR_ENABLED_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanDynamodbTableWithoutBackupRuleConfigTest {

    @Test
    void testThatRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig =
                ScanDynamodbTableWithoutBackupRuleConfig.builder().build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfBackupPlanEnabled(false)
                .passIfPitrEnabled(true)
                .build();

        final var expectedConfigMap = Map.of(
                PASS_IF_PITR_ENABLED_CONFIG_KEY, true,
                PASS_IF_BACKUP_PLAN_ENABLED_CONFIG_KEY, false);

        // When
        final var actualConfigMap = expectedConfig.getConfigAsMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatRuleConfigHasDefaultValues() {
        // Given
        final var expectedConfig = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfPitrEnabled(true)
                .passIfBackupPlanEnabled(true)
                .build();

        // When
        final var actualConfig =
                ScanDynamodbTableWithoutBackupRuleConfig.builder().build();

        // Then
        assertThat(actualConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
    }

    @Test
    void testThatRuleConfigValidationPassesGivenValidRuleConfig() {
        // Given
        final var ruleConfig =
                ScanDynamodbTableWithoutBackupRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(ruleConfig::validate);
    }
}
