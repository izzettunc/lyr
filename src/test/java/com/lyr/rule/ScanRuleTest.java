package com.lyr.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.TestUtil;
import com.lyr.rule.outcome.Outcome;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanRuleTest {

    final RuleStrategy ruleStrategy = mock(RuleStrategy.class);
    final RuleReport ruleReport = mock(RuleReport.class);
    final RuleConfig ruleConfig = mock(RuleConfig.class);
    ScanRule testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanRule(TestUtil.DUMMY_STRING, ruleConfig, ruleStrategy, ruleReport);
    }

    @AfterEach
    public void afterEach() {
        reset(ruleStrategy, ruleReport, ruleConfig);
    }

    @Test
    void testThatEvaluatingScanRuleCachesAndReturnsTheOutcome() {
        // Given
        final List<Outcome> expectedOutcome =
                ImmutableList.of(new ScanOutcome(TestUtil.DUMMY_STRING), new ScanOutcome(TestUtil.DUMMY2_STRING));
        assertThat(testObject.outcome).isNull();

        // When
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        final var actualOutcome = testObject.evaluate();

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
    void testThatEvaluatingScanRuleFiveTimesJustReturnsCachedOutcome() {
        // Given
        final List<Outcome> expectedOutcome =
                ImmutableList.of(new ScanOutcome(TestUtil.DUMMY_STRING), new ScanOutcome(TestUtil.DUMMY2_STRING));
        final List<Outcome> updatedOutcome = ImmutableList.of(
                new ScanOutcome(TestUtil.UPDATED_DUMMY_STRING), new ScanOutcome(TestUtil.UPDATED_DUMMY2_STRING));
        final var amountOfReevaluation = 5;
        assertThat(testObject.outcome).isNull();

        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        final var initialOutcome = testObject.evaluate();

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
        List<ScanOutcome> actualOutcome = ImmutableList.of();
        for (int i = 0; i < amountOfReevaluation; i++) {
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
        final var expectedOutcome =
                ImmutableList.of(new ScanOutcome(TestUtil.DUMMY_STRING), new ScanOutcome(TestUtil.DUMMY2_STRING));
        assertThat(testObject.outcome).isNull();

        // When
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        final var actualOutcome = testObject.reevaluate();

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
    void testThatReevaluatingScanRuleMultipleTimeChangesOutcomeEachTime() {
        // Given
        final var expectedOutcome =
                ImmutableList.of(new ScanOutcome(TestUtil.DUMMY_STRING), new ScanOutcome(TestUtil.DUMMY2_STRING));
        final var expectedUpdatedOutcome = ImmutableList.of(
                new ScanOutcome(TestUtil.UPDATED_DUMMY_STRING), new ScanOutcome(TestUtil.UPDATED_DUMMY2_STRING));
        final var expectedUpdatedOutcomeLast = ImmutableList.of(
                new ScanOutcome(TestUtil.FINAL_UPDATED_DUMMY_STRING),
                new ScanOutcome(TestUtil.FINAL_UPDATED_DUMMY2_STRING));
        final var amountOfReevaluation = 3;
        assertThat(testObject.outcome).isNull();

        // When
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        final var initialOutcome = testObject.reevaluate();
        final var initialOutcomeCached = ImmutableList.copyOf(testObject.outcome);

        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedUpdatedOutcome);
        final var updatedOutcome = testObject.reevaluate();
        final var updatedOutcomeCached = ImmutableList.copyOf(testObject.outcome);

        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedUpdatedOutcomeLast);
        final var updatedOutcomeLast = testObject.reevaluate();
        final var updatedOutcomeCachedLast = ImmutableList.copyOf(testObject.outcome);

        // Then
        assertThat(initialOutcomeCached)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedOutcome)
                .isEqualTo(initialOutcome);

        assertThat(updatedOutcomeCached)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedOutcome)
                .isEqualTo(updatedOutcome);

        assertThat(updatedOutcomeCachedLast)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedOutcomeLast)
                .isEqualTo(updatedOutcomeLast);

        verify(ruleStrategy, times(amountOfReevaluation)).execute(ruleConfig);
    }

    @Test
    void testThatReportReportsSuccessfullyWhenOutcomeIsAlreadyCalculated() {
        // Given
        final List<Outcome> expectedOutcome =
                ImmutableList.of(new ScanOutcome(TestUtil.DUMMY_STRING), new ScanOutcome(TestUtil.DUMMY2_STRING));
        final var expectedReport = "dummyReport";

        assertThat(testObject.outcome).isNull();
        when(ruleStrategy.execute(ruleConfig)).thenReturn(expectedOutcome);
        testObject.evaluate();

        assertThat(testObject.outcome).isNotNull();

        // When
        when(ruleReport.report(ruleConfig, expectedOutcome)).thenReturn(expectedReport);
        final var actualReport = testObject.report();

        // Then
        assertThat(actualReport).isEqualTo(expectedReport);

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
