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
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
        final var expectOptionalRuleConfig =
                Optional.of(ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                        .disallowedArchitecture("arm64")
                        .build());

        // When
        final var actualOptionalRuleConfig = testObject.parse(input);

        // Then
        assertThat(actualOptionalRuleConfig).isEqualTo(expectOptionalRuleConfig);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForRuleCreatorToParse")
    void testThatRuleConfigCreatorReturnsEmptyOptionalWhenInputIsInvalid(final Object input) {
        // Given input
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

    private static Stream<Arguments> invalidInputsForRuleCreatorToParse() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of("An input that is not even a map"),
                Arguments.of(Map.of(DISALLOWED_ARCHITECTURE_CONFIG_KEY, 123)));
    }
}
