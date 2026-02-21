package com.example.rule.lambda.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidateLambdaFunctionConcurrencyRuleConfigTest {

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleConfigIsParsedCorrectly() {
        // Given
        Map<String, Integer> config = Map.of("lambda1", 1, "lambda2", 2);
        var listOfLambdaFunctionConcurrency = List.of(
                new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("lambda1", 1),
                new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency("lambda2", 2));

        RuleConfig expectedRuleConfig = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(listOfLambdaFunctionConcurrency)
                .build();

        // When
        var actualRuleConfig = ValidateLambdaFunctionConcurrencyRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateLambdaFunctionConcurrencyRuleConfig.class)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatValidateLambdaFunctionConcurrencyRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        List<String> invalidConfig = List.of("lambda1", "lambda2");

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionConcurrencyRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
