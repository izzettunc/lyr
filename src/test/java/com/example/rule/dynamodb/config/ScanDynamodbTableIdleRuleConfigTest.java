package com.example.rule.dynamodb.config;

import com.example.rule.RuleConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScanDynamodbTableIdleRuleConfigTest {

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsParsedCorrectly() {
        // Given
        int maxIdlePeriodInDays = 180;
        boolean excludeEmptyTables = true;
        Map<String, Object> config = Map.of("maxIdlePeriodInDays", maxIdlePeriodInDays, "excludeEmptyTables", excludeEmptyTables);
        RuleConfig expectedRuleConfig = ScanDynamodbTableIdleRuleConfig
                .builder()
                .maxIdlePeriodInDays(maxIdlePeriodInDays)
                .excludeEmptyTables(excludeEmptyTables)
                .build();

        // When
        var actualRuleConfig = ScanDynamodbTableIdleRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanDynamodbTableIdleRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        List<Integer> invalidConfig = List.of(15);

        // When & Then
        assertThatThrownBy(() -> ScanDynamodbTableIdleRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigThrowsIllegalArgumentExceptionWhenConfigHasMissingValues() {
        // Given
        Map<String, Object> configMissingMandatoryAttributes = Map.of();

        // When & Then
        assertThatThrownBy(() -> ScanDynamodbTableIdleRuleConfig.parse(configMissingMandatoryAttributes))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsParsedCorrectlyWithoutOptionalAttributes() {
        // Given
        int maxIdlePeriodInDays = 180;
        Map<String, Object> configWithOnlyMandatoryAttributes = Map.of("maxIdlePeriodInDays", maxIdlePeriodInDays);
        RuleConfig expectedRuleConfig = ScanDynamodbTableIdleRuleConfig
                .builder()
                .maxIdlePeriodInDays(maxIdlePeriodInDays)
                .excludeEmptyTables(false)
                .build();

        // When
        var actualRuleConfig = ScanDynamodbTableIdleRuleConfig.parse(configWithOnlyMandatoryAttributes);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanDynamodbTableIdleRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }
}
