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
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ScanDynamodbTableIdleRuleConfigCreatorTest {

    ScanDynamodbTableIdleRuleConfigCreator testObject;
    static MockedStatic<BadRuleConfigException> mockedBadRuleConfigExceptionStatic =
            mockStatic(BadRuleConfigException.class, CALLS_REAL_METHODS);

    @BeforeEach
    void beforeEach() {
        testObject = new ScanDynamodbTableIdleRuleConfigCreator();
        mockedBadRuleConfigExceptionStatic.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedBadRuleConfigExceptionStatic.closeOnDemand();
    }

    @Test
    void testThatRuleConfigCreatorParsesInputCorrectly() {
        // Given
        final Object input = Map.of(MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY, 5, EXCLUDE_EMPTY_TABLES_CONFIG_KEY, true);
        final var expectOptionalRuleConfig = Optional.of(ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(5)
                .excludeEmptyTables(true)
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
        final var expectedOptionalRuleConfig =
                Optional.of(ScanDynamodbTableIdleRuleConfig.builder().build());

        // When
        final var actualOptionalRuleConfig = testObject.parse(configWithoutAttributes);

        // Then
        assertThat(actualOptionalRuleConfig).isEqualTo(expectedOptionalRuleConfig);
    }

    @Test
    void testThatRuleConfigCreatorCreatesDefaultRuleConfigCorrectly() {
        // Given
        final var expectedDefaultRuleConfig =
                ScanDynamodbTableIdleRuleConfig.builder().build();

        // When
        final var actualRuleConfig = testObject.createDefaultConfig();

        // Then
        assertThat(actualRuleConfig).isEqualTo(expectedDefaultRuleConfig);
        assertThat(actualRuleConfig).isNotSameAs(expectedDefaultRuleConfig);
    }

    @Test
    void testThatRuleConfigCreatorValidationPassesGivenValidRuleConfig() {
        // Given
        final var ruleConfig = ScanDynamodbTableIdleRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(() -> testObject.validate(ruleConfig));
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenNullRuleConfig() {
        // Given
        final ScanDynamodbTableIdleRuleConfig ruleConfig = null;

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig)).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(() -> BadRuleConfigException.forNull(anyString()), times(1));
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenRuleConfigWithNegativeMaxIdlePeriodInDays() {
        // Given
        final ScanDynamodbTableIdleRuleConfig ruleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(-100)
                .build();

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig)).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(
                () -> BadRuleConfigException.forLessThanLimit(anyString(), anyString(), anyInt()), times(1));
    }
}
