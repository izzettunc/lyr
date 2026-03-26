package com.lyr.rule.lambda.config;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidateLambdaFunctionExistsRuleConfigTest {

    @Test
    void testThatValidateLambdaFunctionExistsRuleConfigIsParsedCorrectly() {
        // Given
        final List<String> config = List.of(FUNCTION_1, FUNCTION_2);
        final RuleConfig expectedRuleConfig = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(config)
                .build();

        // When
        final var actualRuleConfig = ValidateLambdaFunctionExistsRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateLambdaFunctionExistsRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatValidateLambdaFunctionExistsRuleConfigThrowsIllegalArgumentExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final Map<String, String> invalidConfig = Map.of("abc", "def");

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionExistsRuleConfig.parse(invalidConfig))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
