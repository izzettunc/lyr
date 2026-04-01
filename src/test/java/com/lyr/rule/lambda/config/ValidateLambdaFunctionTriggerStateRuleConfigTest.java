package com.lyr.rule.lambda.config;

import static com.lyr.TestUtil.DISABLED;
import static com.lyr.TestUtil.ENABLED;
import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.rule.RuleConfig;
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
            testThatValidateLambdaFunctionTriggerStateRuleConfigThrowsInvalidRuleConfigTypeExceptionExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final List<String> invalidConfig = List.of(FUNCTION_1, FUNCTION_2);

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(invalidConfig))
                .isInstanceOf(InvalidRuleConfigTypeException.class);
    }

    @Test
    void
            testThatValidateLambdaFunctionTriggerStateRuleConfigThrowsBadRuleConfigExceptionWhenOtherThanEnabledOrDisabledIsProvided() {
        // Given
        final Map<String, String> invalidConfig = Map.of(FUNCTION_1, "Other", FUNCTION_2, DISABLED);

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(invalidConfig))
                .isInstanceOf(BadRuleConfigException.class);
    }
}
