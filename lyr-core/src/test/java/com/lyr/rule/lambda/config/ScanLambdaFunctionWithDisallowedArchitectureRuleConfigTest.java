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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ScanLambdaFunctionWithDisallowedArchitectureRuleConfigTest {

    static MockedStatic<BadRuleConfigException> mockedBadRuleConfigExceptionStatic =
            mockStatic(BadRuleConfigException.class, CALLS_REAL_METHODS);

    @BeforeEach
    void beforeEach() {
        mockedBadRuleConfigExceptionStatic.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedBadRuleConfigExceptionStatic.closeOnDemand();
    }

    @Test
    void testThatRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var expectedConfig = ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                .disallowedArchitecture("arm64")
                .build();
        final var expectedConfigMap = Map.of(DISALLOWED_ARCHITECTURE_CONFIG_KEY, "arm64");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatRuleConfigValidationPassesGivenValidRuleConfig() {
        // Given
        final var ruleConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(ruleConfig::validate);
    }

    @Test
    void testThatRuleConfigValidationFailsGivenRuleConfigWithUnsupportedArchitecture() {
        // Given
        final ScanLambdaFunctionWithDisallowedArchitectureRuleConfig ruleConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                        .disallowedArchitecture("skynet")
                        .build();

        // When & Then
        assertThatThrownBy(ruleConfig::validate).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(
                () -> BadRuleConfigException.forUnsupportedValues(anyString(), anyString(), anyCollection()), times(1));
    }
}
