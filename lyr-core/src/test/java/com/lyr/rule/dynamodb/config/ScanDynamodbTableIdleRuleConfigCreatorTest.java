package com.lyr.rule.dynamodb.config;

import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.EXCLUDE_EMPTY_TABLES_CONFIG_KEY;
import static com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig.MAX_IDLE_PERIOD_IN_DAYS_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanDynamodbTableIdleRuleConfigCreatorTest {

    ScanDynamodbTableIdleRuleConfigCreator testObject;

    @BeforeEach
    void beforeEach() {
        testObject = new ScanDynamodbTableIdleRuleConfigCreator();
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
        assertThatThrownBy(() -> testObject.validate(ruleConfig))
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage("Rule config must not be null");
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenRuleConfigWithNegativeMaxIdlePeriodInDays() {
        // Given
        final ScanDynamodbTableIdleRuleConfig ruleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(-100)
                .build();

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig))
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage(
                        "Max idle period attribute of scan.dynamodb.table.idle rule config, must be longer than a day");
    }
}
