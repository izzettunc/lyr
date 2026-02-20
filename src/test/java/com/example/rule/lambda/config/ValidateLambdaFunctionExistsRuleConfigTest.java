package com.example.rule.lambda.config;

import com.example.rule.RuleConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidateLambdaFunctionExistsRuleConfigTest {

    @Test
    void testThatValidateLambdaFunctionExistsRuleConfigIsParsedCorrectly() {
        // Given
        List<String> config = List.of("lambdaFunction1", "lambdaFunction2");
        RuleConfig expectedRuleConfig = ValidateLambdaFunctionExistsRuleConfig
                .builder()
                .functionNames(config)
                .build();

        // When
        var actualRuleConfig = ValidateLambdaFunctionExistsRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateLambdaFunctionExistsRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatValidateLambdaFunctionExistsRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        Map<String, String> invalidConfig = Map.of("abc", "def");

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionExistsRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }

}