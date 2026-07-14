package com.lyr.rule.glue.config;

import static com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
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

class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigTest {

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
        final var expectedConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(123)
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
        final var expectedConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(123)
                .build();
        final var expectedConfigMap = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY, "123");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatRuleConfigHasDefaultValues() {
        // Given
        final var expectedConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(15)
                .build();

        // When
        final var actualConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();

        // Then
        assertThat(actualConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
    }

    @Test
    void testThatRuleConfigValidationPassesGivenValidRuleConfig() {
        // Given
        final var ruleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(ruleConfig::validate);
    }

    @Test
    void testThatRuleConfigValidationFailsGivenRuleConfigWithNegativeMaxIdleTimeoutInMinutes() {
        // Given
        final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(-1)
                        .build();

        // When & Then
        assertThatThrownBy(ruleConfig::validate).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(
                () -> BadRuleConfigException.forLessThanLimit(anyString(), anyString(), anyInt()), times(1));
    }
}
