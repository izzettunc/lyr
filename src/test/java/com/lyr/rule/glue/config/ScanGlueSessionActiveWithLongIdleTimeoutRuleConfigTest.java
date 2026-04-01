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

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsParsedCorrectly() {
        // Given
        final int maxIdleTimeoutInMinutes = 15;
        final Map<String, Integer> config = Map.of("maxIdleTimeoutInMinutes", maxIdleTimeoutInMinutes);
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
        final Map<String, Integer> config = Map.of("maxIdleTimeoutInMinutes", -1);

        assertThatThrownBy(() -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config))
                .isInstanceOf(BadRuleConfigException.class);
    }
}
