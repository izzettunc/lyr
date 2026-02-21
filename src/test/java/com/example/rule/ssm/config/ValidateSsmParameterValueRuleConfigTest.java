package com.example.rule.ssm.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidateSsmParameterValueRuleConfigTest {

    @Test
    void testThatValidateSsmParameterValueRuleConfigIsParsedCorrectly() {
        // Given
        Map<String, String> config = Map.of("parameter1", "value1", "parameter2", "value2");
        var listOfSsmParameterValue = List.of(
                new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter1", "value1"),
                new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter2", "value2"));

        RuleConfig expectedRuleConfig = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(listOfSsmParameterValue)
                .build();

        // When
        var actualRuleConfig = ValidateSsmParameterValueRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateSsmParameterValueRuleConfig.class)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatValidateSsmParameterValueRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        List<String> invalidConfig = List.of("lambda1", "lambda2");

        // When & Then
        assertThatThrownBy(() -> ValidateSsmParameterValueRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
