package com.example.rule.glue.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigTest {

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsParsedCorrectly() {
        // Given
        int maxIdleTimeoutInMinutes = 15;
        Map<String, Integer> config = Map.of("maxIdleTimeoutInMinutes", maxIdleTimeoutInMinutes);
        RuleConfig expectedRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeoutInMinutes)
                .build();

        // When
        var actualRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        List<Integer> invalidConfig = List.of(15);

        // When & Then
        assertThatThrownBy(() -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigThrowsIllegalArgumentExceptionWhenConfigHasMissingValues() {
        // Given
        Map<String, Integer> configWithoutMandatoryAttributes = Map.of();

        // When & Then
        assertThatThrownBy(() ->
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(configWithoutMandatoryAttributes))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
