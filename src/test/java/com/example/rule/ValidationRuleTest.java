package com.example.rule;

import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ValidationOutcome;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ValidationRuleTest {
    private enum TestEnum {
        ONE, TWO, THREE
    }

    RuleStrategy ruleStrategy = mock(RuleStrategy.class);
    RuleReport ruleReport = mock(RuleReport.class);
    RuleConfig ruleConfig = mock(RuleConfig.class);
    ValidationRule<TestEnum> testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidationRule<>("test", ruleConfig, ruleStrategy, ruleReport);
    }

    @AfterEach
    public void afterEach() {
        reset(ruleStrategy, ruleReport, ruleConfig);
    }

    @Test
    void testThatEvaluatingValidationRuleCachesAndReturnsTheOutcome() {
        // Given
        ImmutableList<Outcome> expectedOutcome = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.invalid(TestEnum.ONE));
        assertThat(testObject.outcome).isNull();

        // When
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        var actualOutcome = testObject.evaluate();

        // Then
        assertThat(testObject.outcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        verify(ruleStrategy, times(1)).execute(ruleConfig);
    }

    @Test
    void testThatEvaluatingValidationRuleMultipleTimeJustReturnsCachedOutcome() {
        // Given
        ImmutableList<Outcome> expectedOutcome = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.invalid(TestEnum.ONE));
        ImmutableList<Outcome> updatedOutcome = ImmutableList.of(ValidationOutcome.invalid(TestEnum.TWO), ValidationOutcome.valid(TestEnum.THREE));
        assertThat(testObject.outcome).isNull();

        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        var initialOutcome = testObject.evaluate();

        assertThat(testObject.outcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        assertThat(initialOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        // When
        when(ruleStrategy.execute(ruleConfig)).thenReturn(updatedOutcome);
        ImmutableList<ValidationOutcome<TestEnum>> actualOutcome = ImmutableList.of();
        for (int i = 0; i < 5; i++) {
            actualOutcome = testObject.evaluate();
        }

        // Then
        assertThat(testObject.outcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        verify(ruleStrategy, times(1)).execute(ruleConfig);
    }

    @Test
    void testThatReevaluateCachesAndReturnsTheOutcome() {
        // Given
        ImmutableList<Outcome> expectedOutcome = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.invalid(TestEnum.ONE));
        assertThat(testObject.outcome).isNull();

        // When
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        var actualOutcome = testObject.reevaluate();

        // Then
        assertThat(testObject.outcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        assertThat(actualOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        verify(ruleStrategy, times(1)).execute(ruleConfig);
    }

    @Test
    void testThatReevaluatingValidationRuleMultipleTimeChangesOutcomeEachTime() {
        // Given
        ImmutableList<Outcome> expectedOutcome = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.invalid(TestEnum.ONE));
        ImmutableList<Outcome> expectedUpdatedOutcome = ImmutableList.of(ValidationOutcome.invalid(TestEnum.TWO), ValidationOutcome.valid(TestEnum.THREE));
        ImmutableList<Outcome> expectedUpdatedOutcomeLast = ImmutableList.of(ValidationOutcome.valid(TestEnum.ONE), ValidationOutcome.invalid(TestEnum.THREE));
        assertThat(testObject.outcome).isNull();

        // When
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        var initialOutcome = testObject.reevaluate();
        var initialOutcomeCached = ImmutableList.copyOf(testObject.outcome);

        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedUpdatedOutcome);
        var updatedOutcome = testObject.reevaluate();
        var updatedOutcomeCached = ImmutableList.copyOf(testObject.outcome);

        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedUpdatedOutcomeLast);
        var updatedOutcomeLast = testObject.reevaluate();
        var updatedOutcomeCachedLast = ImmutableList.copyOf(testObject.outcome);

        // Then
        assertThat(initialOutcomeCached)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        assertThat(initialOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome);

        assertThat(updatedOutcomeCached)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedOutcome);

        assertThat(updatedOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedOutcome);

        assertThat(updatedOutcomeCachedLast)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedOutcomeLast);

        assertThat(updatedOutcomeLast)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedOutcomeLast);

        verify(ruleStrategy, times(3)).execute(ruleConfig);
    }

    @Test
    void testThatReportReportsSuccessfullyWhenOutcomeIsAlreadyCalculated() {
        // Given
        ImmutableList<Outcome> expectedOutcome = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.invalid(TestEnum.ONE));
        var expectedReport = "dummyReport";

        assertThat(testObject.outcome).isNull();
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        testObject.evaluate();

        assertThat(testObject.outcome).isNotNull();

        // When
        when(ruleReport.report(ruleConfig, expectedOutcome)).thenReturn(expectedReport);
        var actualReport = testObject.report();

        // Then
        assertThat(actualReport)
                .isEqualTo(expectedReport);

        verify(ruleReport, times(1)).report(ruleConfig, expectedOutcome);
    }

    @Test
    void testThatReportThrowsIllegalStateExceptionWhenOutcomeIsNotPreviouslyCalculated() {
        // Given
        assertThat(testObject.outcome).isNull();

        // When & Then
        assertThatThrownBy(() -> testObject.report()).isInstanceOf(IllegalStateException.class);

        verify(ruleReport, never()).report(any(), any());
    }
}
