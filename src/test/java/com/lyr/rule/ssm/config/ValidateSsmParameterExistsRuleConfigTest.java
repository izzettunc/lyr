package com.lyr.rule.ssm.config;

import static com.lyr.TestUtil.PARAMETER_1;
import static com.lyr.TestUtil.PARAMETER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidateSsmParameterExistsRuleConfigTest {

    @Test
    void testThatValidateSsmParameterExistsRuleConfigIsParsedCorrectly() {
        // Given
        final List<String> config = List.of(PARAMETER_1, PARAMETER_2);
        final RuleConfig expectedRuleConfig = ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames(config)
                .build();

        // When
        final var actualRuleConfig = ValidateSsmParameterExistsRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateSsmParameterExistsRuleConfig.class)
                .usingRecursiveComparison()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatValidateSsmParameterExistsRuleConfigThrowsInvalidRuleConfigTypeExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final Map<String, String> invalidConfig = Map.of("abc", "def");

        // When & Then
        assertThatThrownBy(() -> ValidateSsmParameterExistsRuleConfig.parse(invalidConfig))
                .isInstanceOf(InvalidRuleConfigTypeException.class);
    }
}
