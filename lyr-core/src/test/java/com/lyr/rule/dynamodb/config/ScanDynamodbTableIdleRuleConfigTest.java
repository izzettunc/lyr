package com.lyr.rule.dynamodb.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.exception.rule.config.MissingMandatoryRuleConfigAttributeException;
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
        final var actualRuleConfig = ScanDynamodbTableIdleRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanDynamodbTableIdleRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigThrowsInvalidRuleConfigTypeExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final List<Integer> invalidConfig = List.of(15);

        // When & Then
        assertThatThrownBy(() -> ScanDynamodbTableIdleRuleConfig.parse(invalidConfig))
                .isInstanceOf(InvalidRuleConfigTypeException.class);
    }

    @Test
    void
            testThatScanDynamodbTableIdleRuleConfigThrowsMissingMandatoryRuleConfigAttributeExceptionWhenConfigHasMissingValues() {
        // Given
        final Map<String, Object> configMissingMandatoryAttributes = Map.of();

        // When & Then
        assertThatThrownBy(() -> ScanDynamodbTableIdleRuleConfig.parse(configMissingMandatoryAttributes))
                .isInstanceOf(MissingMandatoryRuleConfigAttributeException.class);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigThrowsBadRuleConfigExceptionWhenLessThanADayProvidedAsAPeriod() {
        // Given
        final int maxIdlePeriodInDays = -5;
        final boolean excludeEmptyTables = true;
        final Map<String, Object> configWithBadAttribute =
                Map.of(MAX_IDLE_PERIOD_IN_DAYS, maxIdlePeriodInDays, EXCLUDE_EMPTY_TABLES, excludeEmptyTables);

        // When & Then
        assertThatThrownBy(() -> ScanDynamodbTableIdleRuleConfig.parse(configWithBadAttribute))
                .isInstanceOf(BadRuleConfigException.class);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsParsedCorrectlyWithoutOptionalAttributes() {
        // Given
        final int maxIdlePeriodInDays = 180;
        final Map<String, Object> configWithOnlyMandatoryAttributes =
                Map.of(MAX_IDLE_PERIOD_IN_DAYS, maxIdlePeriodInDays);
        final RuleConfig expectedRuleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(maxIdlePeriodInDays)
                .excludeEmptyTables(false)
                .build();

        // When
        final var actualRuleConfig = ScanDynamodbTableIdleRuleConfig.parse(configWithOnlyMandatoryAttributes);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanDynamodbTableIdleRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }
}
