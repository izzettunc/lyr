package com.lyr.util;


import com.lyr.util.exception.rule.UnknownRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleDefinitionTest {

    @Test
    void testThatRuleDefinitionCanBeAcquiredUsingNameSuccessfully(){
        // Given
        final var expectedRuleDefinition = RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

        // When
        final var actualRuleDefinition = RuleDefinition.definitionByName(expectedRuleDefinition.getRuleName());

        // Then
        assertThat(actualRuleDefinition).isSameAs(expectedRuleDefinition);
    }

    @Test
    void testThatRuleDefinitionCanBeAcquiredUsingCodeSuccessfully(){
        // Given
        final var expectedRuleDefinition = RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;

        // When
        final var actualRuleDefinition = RuleDefinition.definitionByCode(expectedRuleDefinition.getRuleCode());

        // Then
        assertThat(actualRuleDefinition).isSameAs(expectedRuleDefinition);
    }

    @Test
    void testThatGivenUnknownRuleNameToAcquireRuleDefinitionUnknownRuleExceptionIsThrown(){
        // Given
        final var invalidName = "I am invalid";

        // When & Then
        assertThatThrownBy(() -> RuleDefinition.definitionByName(invalidName))
                .isInstanceOf(UnknownRuleException.class)
                .hasMessage("Given rule is not defined. Rule name: I am invalid");
    }

    @Test
    void testThatGivenUnknownRuleCodeToAcquireRuleDefinitionUnknownRuleExceptionIsThrown(){
        // Given
        final var invalidCode = "I am invalid";

        // When & Then
        assertThatThrownBy(() -> RuleDefinition.definitionByCode(invalidCode))
                .isInstanceOf(UnknownRuleException.class)
                .hasMessage("Given rule is not defined. Rule code: I am invalid");
    }
}