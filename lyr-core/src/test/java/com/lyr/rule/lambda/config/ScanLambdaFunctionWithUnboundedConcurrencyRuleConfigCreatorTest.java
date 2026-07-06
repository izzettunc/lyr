package com.lyr.rule.lambda.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.exception.rule.config.BadRuleConfigException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreatorTest {

    ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator testObject;
    static MockedStatic<BadRuleConfigException> mockedBadRuleConfigExceptionStatic =
            mockStatic(BadRuleConfigException.class, CALLS_REAL_METHODS);

    @BeforeEach
    void beforeEach() {
        testObject = new ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator();
        mockedBadRuleConfigExceptionStatic.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedBadRuleConfigExceptionStatic.closeOnDemand();
    }

    @Test
    void testThatRuleConfigCreatorParsesInputCorrectly() {
        // Given
        final Object input = "This can be null or anything doesn't matter";

        // When
        final var actualOptionalRuleConfig = testObject.parse(input);

        // Then
        assertThat(actualOptionalRuleConfig).isEmpty();
    }

    @Test
    void testThatRuleConfigCreatorCreatesDefaultRuleConfigCorrectly() {
        // Given
        final var expectedDefaultRuleConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        // When
        final var actualRuleConfig = testObject.createDefaultConfig();

        // Then
        assertThat(actualRuleConfig).isEqualTo(expectedDefaultRuleConfig);
        assertThat(actualRuleConfig).isNotSameAs(expectedDefaultRuleConfig);
    }

    @Test
    void testThatRuleConfigCreatorValidationPassesGivenValidRuleConfig() {
        // Given
        final var ruleConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(() -> testObject.validate(ruleConfig));
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenInvalidRuleConfig() {
        // Given
        final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ruleConfig = null;

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig)).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(() -> BadRuleConfigException.forNull(anyString()), times(1));
    }
}
