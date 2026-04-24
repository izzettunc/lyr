package com.lyr.rule;

import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.lyr.config.RuleSetConfig;
import com.lyr.exception.rule.UnknownRuleException;
import com.lyr.rule.dynamodb.ScanDynamodbTableIdleRuleExecution;
import com.lyr.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class RuleFactoryTest {

    final RuleSetConfig mockedRuleSetConfig = mock(RuleSetConfig.class);

    @Test
    void testThatRuleFactoryThrowsUnknownRuleExceptionWhenUnknownRuleNameIsProvided() {
        // Given
        try (MockedStatic<RuleSetConfig> configMockedStatic = Mockito.mockStatic(RuleSetConfig.class)) {
            configMockedStatic.when(RuleSetConfig::getInstance).thenReturn(mockedRuleSetConfig);

            final var unknownRuleName = "unknownRuleName";

            // When & Then
            assertThatThrownBy(() -> RuleFactory.createRule(unknownRuleName)).isInstanceOf(UnknownRuleException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("allRulesAndExceptedClasses")
    void testThatRuleFactoryCreatesAllAvailableRules(
            final String ruleName, final Class<?> expectedTypeOfRule, final Class<?> expectedTypeOfRuleImpl) {
        // Given
        // rulename, expectedTypeOfRule, expectedTypeOfRuleImpl
        try (MockedStatic<RuleSetConfig> configMockedStatic = Mockito.mockStatic(RuleSetConfig.class)) {
            configMockedStatic.when(RuleSetConfig::getInstance).thenReturn(mockedRuleSetConfig);

            // When
            final var rule = RuleFactory.createRule(ruleName);

            // Then
            assertThat(rule).isNotNull().isInstanceOf(expectedTypeOfRule);
            assertThat(rule.getRuleExecutionStrategy()).isNotNull().isInstanceOf(expectedTypeOfRuleImpl);
        }
    }

    public static Stream<Arguments> allRulesAndExceptedClasses() {
        return Stream.of(
                Arguments.of(
                        SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT.getRuleName(),
                        Rule.class,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution.class),
                Arguments.of(
                        SCAN_DYNAMODB_TABLE_IDLE.getRuleName(), Rule.class, ScanDynamodbTableIdleRuleExecution.class),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY.getRuleName(),
                        Rule.class,
                        ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution.class));
    }
}
