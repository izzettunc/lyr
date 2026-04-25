package com.lyr.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lyr.TestUtil;
import com.lyr.report.model.Execution;
import com.lyr.report.model.Finding;
import com.lyr.util.RuleDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuleTest {

    final RuleExecutionStrategy ruleExecutionStrategy = mock(RuleExecutionStrategy.class);
    final RuleConfig ruleConfig = mock(RuleConfig.class);
    Rule testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new Rule(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE, ruleConfig, ruleExecutionStrategy);
    }

    @AfterEach
    public void afterEach() {
        reset(ruleExecutionStrategy, ruleConfig);
    }

    @Test
    void testThatEvaluatingScanRuleCachesAndReturnsTheOutcome() {
        // Given
        final var expectedFindings =
                ImmutableList.of(Finding.byId(TestUtil.DUMMY_STRING), Finding.byId(TestUtil.DUMMY2_STRING));
        final var expectedExecution = Execution.builder()
                .findings(expectedFindings)
                .name(testObject.ruleDefinition.getRuleName())
                .code(testObject.ruleDefinition.getRuleCode())
                .configuration(ImmutableMap.of())
                .build();
        assertThat(testObject.execution).isNull();

        // When
        when(ruleExecutionStrategy.execute(ruleConfig)).thenReturn(expectedFindings);
        final var actualExecution = testObject.evaluate();

        // Then
        assertThat(testObject.execution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        assertThat(actualExecution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        verify(ruleExecutionStrategy, times(1)).execute(ruleConfig);
    }

    @Test
    void testThatEvaluatingScanRuleFiveTimesJustReturnsCachedOutcome() {
        // Given
        final var expectedFindings =
                ImmutableList.of(Finding.byId(TestUtil.DUMMY_STRING), Finding.byId(TestUtil.DUMMY2_STRING));
        final var expectedExecution = Execution.builder()
                .findings(expectedFindings)
                .name(testObject.ruleDefinition.getRuleName())
                .code(testObject.ruleDefinition.getRuleCode())
                .configuration(ImmutableMap.of())
                .build();

        final var updatedFindings = ImmutableList.of(
                Finding.byId(TestUtil.UPDATED_DUMMY_STRING), Finding.byId(TestUtil.UPDATED_DUMMY2_STRING));

        final var amountOfReevaluation = 5;
        assertThat(testObject.execution).isNull();

        when(ruleExecutionStrategy.execute(ruleConfig)).thenReturn(expectedFindings);
        final var initialOutcome = testObject.evaluate();

        assertThat(testObject.execution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        assertThat(initialOutcome)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        // When
        when(ruleExecutionStrategy.execute(ruleConfig)).thenReturn(updatedFindings);
        Execution actualExecution = null;
        for (int i = 0; i < amountOfReevaluation; i++) {
            actualExecution = testObject.evaluate();
        }

        // Then
        assertThat(testObject.execution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        assertThat(actualExecution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        verify(ruleExecutionStrategy, times(1)).execute(ruleConfig);
    }

    @Test
    void testThatReevaluateCachesAndReturnsTheOutcome() {
        // Given
        final var expectedFindings =
                ImmutableList.of(Finding.byId(TestUtil.DUMMY_STRING), Finding.byId(TestUtil.DUMMY2_STRING));
        final var expectedExecution = Execution.builder()
                .findings(expectedFindings)
                .name(testObject.ruleDefinition.getRuleName())
                .code(testObject.ruleDefinition.getRuleCode())
                .configuration(ImmutableMap.of())
                .build();
        assertThat(testObject.execution).isNull();

        // When
        when(ruleExecutionStrategy.execute(ruleConfig)).thenReturn(expectedFindings);
        final var actualExecution = testObject.reevaluate();

        // Then
        assertThat(testObject.execution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        assertThat(actualExecution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution);

        verify(ruleExecutionStrategy, times(1)).execute(ruleConfig);
    }

    @Test
    void testThatReevaluatingScanRuleMultipleTimeChangesOutcomeEachTime() {
        // Given
        final var expectedFindings =
                ImmutableList.of(Finding.byId(TestUtil.DUMMY_STRING), Finding.byId(TestUtil.DUMMY2_STRING));
        final var expectedExecution = Execution.builder()
                .findings(expectedFindings)
                .name(testObject.ruleDefinition.getRuleName())
                .code(testObject.ruleDefinition.getRuleCode())
                .configuration(ImmutableMap.of())
                .build();

        final var expectedUpdatedFindings = ImmutableList.of(
                Finding.byId(TestUtil.UPDATED_DUMMY_STRING), Finding.byId(TestUtil.UPDATED_DUMMY2_STRING));
        final var expectedUpdatedExecution = Execution.builder()
                .findings(expectedUpdatedFindings)
                .name(testObject.ruleDefinition.getRuleName())
                .code(testObject.ruleDefinition.getRuleCode())
                .configuration(ImmutableMap.of())
                .build();

        final var expectedUpdatedFindingsLast = ImmutableList.of(
                Finding.byId(TestUtil.FINAL_UPDATED_DUMMY_STRING), Finding.byId(TestUtil.FINAL_UPDATED_DUMMY2_STRING));
        final var expectedUpdatedExecutionLast = Execution.builder()
                .findings(expectedUpdatedFindingsLast)
                .name(testObject.ruleDefinition.getRuleName())
                .code(testObject.ruleDefinition.getRuleCode())
                .configuration(ImmutableMap.of())
                .build();

        final var amountOfReevaluation = 3;
        assertThat(testObject.execution).isNull();

        // When
        when(ruleExecutionStrategy.execute(ruleConfig)).thenReturn(expectedFindings);
        final var actualInitialExecution = testObject.reevaluate();
        final var actualInitialExecutionCached = Execution.copyOf(testObject.execution);

        when(ruleExecutionStrategy.execute(ruleConfig)).thenReturn(expectedUpdatedFindings);
        final var actualUpdatedExecution = testObject.reevaluate();
        final var actualUpdatedExecutionCached = Execution.copyOf(testObject.execution);

        when(ruleExecutionStrategy.execute(ruleConfig)).thenReturn(expectedUpdatedFindingsLast);
        final var actualUpdatedExecutionLast = testObject.reevaluate();
        final var actualUpdatedExecutionCachedLast = Execution.copyOf(testObject.execution);

        // Then
        assertThat(actualInitialExecution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecution)
                .isEqualTo(actualInitialExecutionCached);

        assertThat(actualUpdatedExecution)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedExecution)
                .isEqualTo(actualUpdatedExecutionCached);

        assertThat(actualUpdatedExecutionLast)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUpdatedExecutionLast)
                .isEqualTo(actualUpdatedExecutionCachedLast);

        verify(ruleExecutionStrategy, times(amountOfReevaluation)).execute(ruleConfig);
    }
}
