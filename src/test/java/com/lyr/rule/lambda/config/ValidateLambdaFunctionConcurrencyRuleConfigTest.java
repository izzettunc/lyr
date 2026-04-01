package com.lyr.rule.lambda.config;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidateLambdaFunctionConcurrencyRuleConfigTest {

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleConfigIsParsedCorrectly() {
        // Given
        final Map<String, Integer> config = Map.of(FUNCTION_1, 1, FUNCTION_2, 2);
        final var listOfLambdaFunctionConcurrency = List.of(
                new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_1, 1),
                new ValidateLambdaFunctionConcurrencyRuleConfig.LambdaFunctionConcurrency(FUNCTION_2, 2));

        final RuleConfig expectedRuleConfig = ValidateLambdaFunctionConcurrencyRuleConfig.builder()
                .functionConcurrences(listOfLambdaFunctionConcurrency)
                .build();

        // When
        final var actualRuleConfig = ValidateLambdaFunctionConcurrencyRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateLambdaFunctionConcurrencyRuleConfig.class)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatValidateLambdaFunctionConcurrencyRuleConfigThrowsInvalidRuleConfigTypeExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final List<String> invalidConfig = List.of(FUNCTION_1, FUNCTION_2);

        // When & Then
        assertThatThrownBy(() -> ValidateLambdaFunctionConcurrencyRuleConfig.parse(invalidConfig))
                .isInstanceOf(InvalidRuleConfigTypeException.class);
    }
}
