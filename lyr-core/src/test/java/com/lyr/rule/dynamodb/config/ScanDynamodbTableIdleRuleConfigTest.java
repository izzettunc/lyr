package com.lyr.rule.dynamodb.config;

import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.EXCLUDE_EMPTY_TABLES_CONFIG_KEY;
import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY;
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

class ScanDynamodbTableIdleRuleConfigTest {

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
        final var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(123)
                .excludeEmptyTables(true)
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
        final var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(123)
                .excludeEmptyTables(true)
                .build();
        final var expectedConfigMap = Map.of(
                MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, "123",
                EXCLUDE_EMPTY_TABLES_CONFIG_KEY, "true");

        // When
        final var actualConfigMap = expectedConfig.getConfigAsStringMap();

        // Then
        assertThat(actualConfigMap).usingRecursiveComparison().isEqualTo(expectedConfigMap);
    }

    @Test
    void testThatRuleConfigHasDefaultValues() {
        // Given
        final var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(30)
                .excludeEmptyTables(false)
                .build();

        // When
        final var actualConfig = ScanDynamodbTableIdleRuleConfig.builder().build();

        // Then
        assertThat(actualConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
    }

    @Test
    void testThatRuleConfigValidationPassesGivenValidRuleConfig() {
        // Given
        final var ruleConfig = ScanDynamodbTableIdleRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(ruleConfig::validate);
    }

    @Test
    void testThatRuleConfigValidationFailsGivenRuleConfigWithNegativeMaxIdlePeriodInDays() {
        // Given
        final ScanDynamodbTableIdleRuleConfig ruleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(-100)
                .build();

        // When & Then
        assertThatThrownBy(ruleConfig::validate).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(
                () -> BadRuleConfigException.forLessThanLimit(anyString(), anyString(), anyInt()), times(1));
    }
}
