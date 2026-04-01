package com.lyr.rule.ssm.config;

import static com.lyr.TestUtil.PARAMETER_1;
import static com.lyr.TestUtil.PARAMETER_2;
import static com.lyr.TestUtil.VALUE_1;
import static com.lyr.TestUtil.VALUE_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidateSsmParameterValueRuleConfigTest {

    @Test
    void testThatValidateSsmParameterValueRuleConfigIsParsedCorrectly() {
        // Given
        final Map<String, String> config = Map.of(PARAMETER_1, VALUE_1, PARAMETER_2, VALUE_2);
        final var listOfSsmParameterValue = List.of(
                new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_1, VALUE_1),
                new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_2, VALUE_2));

        final RuleConfig expectedRuleConfig = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(listOfSsmParameterValue)
                .build();

        // When
        final var actualRuleConfig = ValidateSsmParameterValueRuleConfig.parse(config);

        // Then
        assertThat(actualRuleConfig)
                .isInstanceOf(ValidateSsmParameterValueRuleConfig.class)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatValidateSsmParameterValueRuleConfigThrowsInvalidRuleConfigTypeExceptionWhenInvalidConfigTypeIsProvided() {
        // Given
        final List<String> invalidConfig = List.of("lambda1", "lambda2");

        // When & Then
        assertThatThrownBy(() -> ValidateSsmParameterValueRuleConfig.parse(invalidConfig))
                .isInstanceOf(InvalidRuleConfigTypeException.class);
    }
}
