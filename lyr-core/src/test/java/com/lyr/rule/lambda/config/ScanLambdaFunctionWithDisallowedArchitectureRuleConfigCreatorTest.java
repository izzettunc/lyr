package com.lyr.rule.lambda.config;

import static com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.DISALLOWED_ARCHITECTURE_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.exception.rule.config.BadRuleConfigException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreatorTest {

    ScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator testObject;
    static MockedStatic<BadRuleConfigException> mockedBadRuleConfigExceptionStatic =
            mockStatic(BadRuleConfigException.class, CALLS_REAL_METHODS);

    @BeforeEach
    void beforeEach() {
        testObject = new ScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator();
        mockedBadRuleConfigExceptionStatic.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedBadRuleConfigExceptionStatic.closeOnDemand();
    }

    @Test
    void testThatRuleConfigCreatorParsesInputCorrectly() {
        // Given
        final Object input = Map.of(DISALLOWED_ARCHITECTURE_CONFIG_KEY, "arm64");
        final var expectOptionalRuleConfig = Optional.of(ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                .disallowedArchitecture("arm64")
                .build());

        // When
        final var actualOptionalRuleConfig = testObject.parse(input);

        // Then
        assertThat(actualOptionalRuleConfig).isEqualTo(expectOptionalRuleConfig);
    }

    @Test
    void testThatRuleConfigCreatorReturnsEmptyOptionalWhenInputIsInvalid() {
        // Given
        final Object input = "This can be null or something invalid";

        // When
        final var actualOptionalRuleConfig = testObject.parse(input);

        // Then
        assertThat(actualOptionalRuleConfig).isEmpty();
    }

    @Test
    void testThatRuleConfigIsParsedCorrectlyWithoutAnyAttribute() {
        // Given
        final Object configWithoutAttributes = Map.of();
        final var expectedOptionalRuleConfig = Optional.of(
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build());

        // When
        final var actualOptionalRuleConfig = testObject.parse(configWithoutAttributes);

        // Then
        assertThat(actualOptionalRuleConfig).isEqualTo(expectedOptionalRuleConfig);
    }

    @Test
    void testThatRuleConfigCreatorCreatesDefaultRuleConfigCorrectly() {
        // Given
        final var expectedDefaultRuleConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build();

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
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(() -> testObject.validate(ruleConfig));
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenNullRuleConfig() {
        // Given
        final ScanLambdaFunctionWithDisallowedArchitectureRuleConfig ruleConfig = null;

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig)).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(() -> BadRuleConfigException.forNull(anyString()), times(1));
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenRuleConfigWithUnsupportedArchitecture() {
        // Given
        final ScanLambdaFunctionWithDisallowedArchitectureRuleConfig ruleConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                        .disallowedArchitecture("skynet")
                        .build();

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig)).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(
                () -> BadRuleConfigException.forUnsupportedValues(anyString(), anyString(), anyCollection()), times(1));
    }
}
