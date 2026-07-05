package com.lyr.rule.dynamodb.config;

import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.EXCLUDE_EMPTY_TABLES_CONFIG_KEY;
import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanDynamodbTableIdleRuleConfigTest {

    @Test
    void testThatRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(123)
                .excludeEmptyTables(true)
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
        final var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(123)
                .excludeEmptyTables(true)
                .build();
        final var expectedConfigMap = Map.of(
                MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, "123",
                EXCLUDE_EMPTY_TABLES_CONFIG_KEY, "true");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatRuleConfigHasDefaultValues() {
        // Given
        final var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(30)
                .excludeEmptyTables(false)
                .build();

        // When
        final var actualConfig = ScanDynamodbTableIdleRuleConfig.builder().build();

        // Then
        assertThat(actualConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
    }
}
