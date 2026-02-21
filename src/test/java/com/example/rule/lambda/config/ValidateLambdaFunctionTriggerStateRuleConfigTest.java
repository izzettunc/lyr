package com.example.rule.lambda.config;


import com.example.rule.RuleConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidateLambdaFunctionTriggerStateRuleConfigTest {

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleConfigIsParsedCorrectly() {
        // Given
        Map<String, String> config = Map.of("lambda1", "Enabled", "lambda2", "Disabled");
        var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda1", true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda2", false)
        );

        RuleConfig expectedRuleConfig = ValidateLambdaFunctionTriggerStateRuleConfig
                .builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        // When
        var actualRuleConfig = ValidateLambdaFunctionTriggerStateRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateLambdaFunctionTriggerStateRuleConfig.class)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        List<String> invalidConfig = List.of("lambda1", "lambda2");

        // When & Then
            assertThatThrownBy(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleConfigThrowsIllegalArgumentExceptionWhenOtherThanEnabledOrDisabledIsProvided() {
        // Given
        Map<String, String> invalidConfig = Map.of("lambda1", "Other", "lambda2", "Disabled");

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }
}