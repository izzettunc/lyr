package com.lyr.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuleConfigCreatorTest {

    RuleConfigCreator testObject = mock(RuleConfigCreator.class);

    @BeforeEach
    void beforeEach() {
        reset(testObject);
        when(testObject.create(any())).thenCallRealMethod();
    }

    @Test
    void testThatRuleConfigCreatorParsesInputAndValidatesTheResult() {
        // Given
        final Object input = new Object();
        final var expectedRuleConfig = mock(RuleConfig.class);

        // When
        when(testObject.parse(input)).thenReturn(Optional.of(expectedRuleConfig));
        final var actualRuleConfig = testObject.create(input);

        // Then
        assertThat(actualRuleConfig).isEqualTo(expectedRuleConfig);
        assertThat(actualRuleConfig).isSameAs(expectedRuleConfig);
        verify(testObject, times(1)).parse(input);
        verify(testObject, times(0)).createDefaultConfig();
        verify(testObject, times(1)).validate(expectedRuleConfig);
    }

    @Test
    void testThatRuleConfigCreatorCreatedDefaultAndValidatesItIfParsingReturnsAnEmptyOptional() {
        // Given
        final Object input = new Object();
        final var expectedRuleConfig = mock(RuleConfig.class);

        // When
        when(testObject.parse(input)).thenReturn(Optional.empty());
        when(testObject.createDefaultConfig()).thenReturn(expectedRuleConfig);
        final var actualRuleConfig = testObject.create(input);

        // Then
        assertThat(actualRuleConfig).isEqualTo(expectedRuleConfig);
        assertThat(actualRuleConfig).isSameAs(expectedRuleConfig);
        verify(testObject, times(1)).parse(input);
        verify(testObject, times(1)).createDefaultConfig();
        verify(testObject, times(1)).validate(expectedRuleConfig);
    }
}
