package com.example.rule.lambda.config;

import static com.example.TestUtil.DISABLED;
import static com.example.TestUtil.ENABLED;
import static com.example.TestUtil.FUNCTION_1;
import static com.example.TestUtil.FUNCTION_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidateLambdaFunctionTriggerStateRuleConfigTest {

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleConfigIsParsedCorrectly() {
        // Given
        final Map<String, String> config = Map.of(FUNCTION_1, ENABLED, FUNCTION_2, DISABLED);
        final var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_1, true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_2, false));

        final RuleConfig expectedRuleConfig = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        // When
        final var actualRuleConfig = ValidateLambdaFunctionTriggerStateRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateLambdaFunctionTriggerStateRuleConfig.class)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatValidateLambdaFunctionTriggerStateRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final List<String> invalidConfig = List.of(FUNCTION_1, FUNCTION_2);

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void
            testThatValidateLambdaFunctionTriggerStateRuleConfigThrowsIllegalArgumentExceptionWhenOtherThanEnabledOrDisabledIsProvided() {
        // Given
        final Map<String, String> invalidConfig = Map.of(FUNCTION_1, "Other", FUNCTION_2, DISABLED);

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
