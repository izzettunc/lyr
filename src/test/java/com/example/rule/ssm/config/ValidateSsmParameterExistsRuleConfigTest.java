package com.example.rule.ssm.config;

import com.example.rule.RuleConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidateSsmParameterExistsRuleConfigTest {

    @Test
    void testThatValidateSsmParameterExistsRuleConfigIsParsedCorrectly() {
        // Given
        List<String> config = List.of("ssmParam1", "ssmParam2");
        RuleConfig expectedRuleConfig = ValidateSsmParameterExistsRuleConfig
                .builder()
                .parameterNames(config)
                .build();

        // When
        var actualRuleConfig = ValidateSsmParameterExistsRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateSsmParameterExistsRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatValidateSsmParameterExistsRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        Map<String, String> invalidConfig = Map.of("abc", "def");

        // When & Then
        assertThatThrownBy(() -> ValidateSsmParameterExistsRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }
}