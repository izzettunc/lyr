package com.lyr.exception.rule.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BadRuleConfigExceptionTest {

    @Test
    void testThatBadRuleConfigExceptionForNullIsFormedCorrectly() {
        // Given
        final var ruleName = "dummy";
        final var expectedMessage = "dummy rule config must not be null";

        // When
        final var actualBadRuleConfigException = BadRuleConfigException.forNull(ruleName);

        // Then
        assertThat(actualBadRuleConfigException)
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void testThatBadRuleConfigExceptionForLessThanLimitIsFormedCorrectly() {
        // Given
        final var ruleName = "dummy";
        final var configName = "dummy config";
        final var limit = 123;
        final var expectedMessage = "dummy config for dummy rule config must be equal or greater than 123";

        // When
        final var actualBadRuleConfigException = BadRuleConfigException.forLessThanLimit(configName, ruleName, limit);

        // Then
        assertThat(actualBadRuleConfigException)
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void testThatBadRuleConfigExceptionForUnsupportedValueIsFormedCorrectly() {
        // Given
        final var ruleName = "dummy";
        final var configName = "dummy config";
        final List<Object> allowedValues = new ArrayList<>(List.of(123, "abc", true));
        final var allowedObject = new Object();
        allowedValues.add(allowedObject);
        allowedValues.add(null);
        final var expectedMessage =
                "dummy config for dummy rule config must be one of [123, \"abc\", true, " + allowedObject + ", null]";

        // When
        final var actualBadRuleConfigException =
                BadRuleConfigException.forUnsupportedValues(configName, ruleName, allowedValues);

        // Then
        assertThat(actualBadRuleConfigException)
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void testThatBadRuleConfigExceptionForUnsupportedValueIsFormedCorrectlyEvenIfCollectiomIsEmpty() {
        // Given
        final var ruleName = "dummy";
        final var configName = "dummy config";
        final var expectedMessage = "dummy config for dummy rule config must be one of []";

        // When
        final var actualBadRuleConfigException =
                BadRuleConfigException.forUnsupportedValues(configName, ruleName, Set.of());

        // Then
        assertThat(actualBadRuleConfigException)
                .isInstanceOf(BadRuleConfigException.class)
                .hasMessage(expectedMessage);
    }
}
