package com.lyr.rule.dynamodb.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanDynamodbTableIdleRuleConfigTest {

    private static final String MAX_IDLE_PERIOD_IN_DAYS = "maxIdlePeriodInDays";
    private static final String EXCLUDE_EMPTY_TABLES = "excludeEmptyTables";

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsParsedCorrectly() {
        // Given
        final int maxIdlePeriodInDays = 180;
        final boolean excludeEmptyTables = true;
        final Map<String, Object> config =
                Map.of(MAX_IDLE_PERIOD_IN_DAYS, maxIdlePeriodInDays, EXCLUDE_EMPTY_TABLES, excludeEmptyTables);
        final RuleConfig expectedRuleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(maxIdlePeriodInDays)
                .excludeEmptyTables(excludeEmptyTables)
                .build();

        // When
        final var actualRuleConfig = ScanDynamodbTableIdleRuleConfig.create(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanDynamodbTableIdleRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigCreatesDefaultOneWhenParsingFails() {
        // Given
        final List<Integer> invalidConfig = List.of(15);
        final var expectedRuleConfig = ScanDynamodbTableIdleRuleConfig.builder().build();

        // When
        final var actualRuleConfig = ScanDynamodbTableIdleRuleConfig.create(invalidConfig);

        // Then
        assertThat(actualRuleConfig).usingRecursiveComparison().isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigThrowsBadRuleConfigExceptionWhenLessThanADayProvidedAsAPeriod() {
        // Given
        final int maxIdlePeriodInDays = -5;
        final boolean excludeEmptyTables = true;
        final Map<String, Object> configWithBadAttribute =
                Map.of(MAX_IDLE_PERIOD_IN_DAYS, maxIdlePeriodInDays, EXCLUDE_EMPTY_TABLES, excludeEmptyTables);

        // When & Then
        assertThatThrownBy(() -> ScanDynamodbTableIdleRuleConfig.create(configWithBadAttribute))
                .isInstanceOf(BadRuleConfigException.class);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsParsedCorrectlyWithoutAnyAttribute() {
        // Given
        final Map<String, Object> configWithoutAttributes = Map.of();
        final RuleConfig expectedRuleConfig =
                ScanDynamodbTableIdleRuleConfig.builder().build();

        // When
        final var actualRuleConfig = ScanDynamodbTableIdleRuleConfig.create(configWithoutAttributes);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanDynamodbTableIdleRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsCopiedCorrectly() {
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
    void testThatScanDynamodbTableIdleRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(123)
                .excludeEmptyTables(true)
                .build();
        final var expectedConfigMap = Map.of(
                MAX_IDLE_PERIOD_IN_DAYS, "123",
                EXCLUDE_EMPTY_TABLES, "true");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigHasDefaultValues() {
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
