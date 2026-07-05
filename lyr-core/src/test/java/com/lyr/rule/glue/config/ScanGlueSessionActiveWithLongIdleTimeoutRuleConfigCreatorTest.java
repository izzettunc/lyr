package com.lyr.rule.glue.config;

import static com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.rule.config.BadRuleConfigException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreatorTest {

    ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator testObject;

    @BeforeEach
    void beforeEach() {
        testObject = new ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator();
    }

    @Test
    void testThatRuleConfigCreatorParsesInputCorrectly() {
        // Given
        final Object input = Map.of(MAX_IDLE_TIMEOUT_IN_MINUTES_CONFIG_KEY, 5);
        final var expectOptionalRuleConfig = Optional.of(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(5)
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
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build());

        // When
        final var actualOptionalRuleConfig = testObject.parse(configWithoutAttributes);

        // Then
        assertThat(actualOptionalRuleConfig).isEqualTo(expectedOptionalRuleConfig);
    }

    @Test
    void testThatRuleConfigCreatorCreatesDefaultRuleConfigCorrectly() {
        // Given
        final var expectedDefaultRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();

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
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();

        // When & Then
        assertThatNoException().isThrownBy(() -> testObject.validate(ruleConfig));
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenNullRuleConfig() {
        // Given
        final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig = null;

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig))
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage("Rule config must not be null");
    }

    @Test
    void testThatRuleConfigCreatorValidationFailsGivenRuleConfigWithNegativeMaxIdleTimeoutInMinutes() {
        // Given
        final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(-1)
                        .build();

        // When & Then
        assertThatThrownBy(() -> testObject.validate(ruleConfig))
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage(
                        "Max idle timeout in minutes attribute of scan.glue.session.activeWithLongIdleTimeout rule config, must be equal or greater than 0");
    }
}
