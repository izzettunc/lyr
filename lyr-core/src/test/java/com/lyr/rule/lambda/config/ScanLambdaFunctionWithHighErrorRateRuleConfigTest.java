package com.lyr.rule.lambda.config;

import static com.lyr.rule.lambda.config.ScanLambdaFunctionWithHighErrorRateRuleConfig.ERROR_RATE_THRESHOLD_CONFIG_KEY;
import static com.lyr.rule.lambda.config.ScanLambdaFunctionWithHighErrorRateRuleConfig.TIME_SPAN_IN_DAYS_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ScanLambdaFunctionWithHighErrorRateRuleConfigTest {
    @Test
    void testThatRuleConfigHasDefaultValues() {
        // Given
        final var expectedConfig = ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .timeSpanInDays(30)
                .errorRateThreshold(50d)
                .build();

        // When
        final var actualConfig =
                ScanLambdaFunctionWithHighErrorRateRuleConfig.builder().build();

        // Then
        assertThat(actualConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
    }

    @Test
    void testThatRuleConfigIsCopiedCorrectly() {
        // Given
        final var expectedConfig = ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .timeSpanInDays(7)
                .errorRateThreshold(2.5)
                .build();

        // When
        final var actualCopiedConfig = expectedConfig.copy();

        // Then
        assertThat(actualCopiedConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(actualCopiedConfig).isNotSameAs(expectedConfig);
    }

    @Test
    void testThatRuleConfigIsConvertedToAMapSuccessfully() {
        // Given
        final var ruleConfig = ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .timeSpanInDays(7)
                .errorRateThreshold(2.5)
                .build();

        final var expectedConfigMap = Map.of(TIME_SPAN_IN_DAYS_CONFIG_KEY, 7, ERROR_RATE_THRESHOLD_CONFIG_KEY, 2.5);

        // When
        final var actualConfigMap = ruleConfig.getConfigAsMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @ParameterizedTest
    @CsvSource({"1, 0", "455, 100", "30, 2.5"})
    void testThatRuleConfigValidationPassesGivenValidTimeSpanInDaysAndErrorRateThreshold(
            final int timeSpanInDays, final double errorRateThreshold) {
        // Given
        final var ruleConfig = ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .timeSpanInDays(timeSpanInDays)
                .errorRateThreshold(errorRateThreshold)
                .build();

        // When & Then
        assertThatNoException().isThrownBy(ruleConfig::validate);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 456})
    void testThatTimeSpanInDaysOutOfBoundsIsRejected(final int timeSpanInDays) {
        // Given
        final var ruleConfig = ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .timeSpanInDays(timeSpanInDays)
                .build();

        // When & Then
        assertThatThrownBy(ruleConfig::validate)
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessageContaining(TIME_SPAN_IN_DAYS_CONFIG_KEY);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 100.1})
    void testThatErrorRateThresholdOutOfBoundsOrNonFiniteIsRejected(final double errorRateThreshold) {
        // Given
        final var ruleConfig = ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .errorRateThreshold(errorRateThreshold)
                .build();

        // When & Then
        assertThatThrownBy(ruleConfig::validate)
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessageContaining(ERROR_RATE_THRESHOLD_CONFIG_KEY);
    }
}
