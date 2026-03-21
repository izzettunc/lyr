package com.lyr.rule;

import static com.lyr.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.lyr.config.RuleSet;
import com.lyr.rule.dynamodb.ScanDynamodbTableIdleRuleImpl;
import com.lyr.rule.dynamodb.report.ScanDynamodbTableIdleRuleReport;
import com.lyr.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl;
import com.lyr.rule.glue.report.ScanGlueSessionActiveWithLongIdleTimeoutRuleReport;
import com.lyr.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl;
import com.lyr.rule.lambda.ValidateLambdaFunctionConcurrencyRuleImpl;
import com.lyr.rule.lambda.ValidateLambdaFunctionExistsRuleImpl;
import com.lyr.rule.lambda.ValidateLambdaFunctionTriggerStateRuleImpl;
import com.lyr.rule.lambda.report.ScanLambdaFunctionWithUnboundedConcurrencyRuleReport;
import com.lyr.rule.lambda.report.ValidateLambdaFunctionConcurrencyRuleReport;
import com.lyr.rule.lambda.report.ValidateLambdaFunctionExistsRuleReport;
import com.lyr.rule.lambda.report.ValidateLambdaFunctionTriggerStateRuleReport;
import com.lyr.rule.ssm.ValidateSsmParameterExistsRuleImpl;
import com.lyr.rule.ssm.ValidateSsmParameterValueRuleImpl;
import com.lyr.rule.ssm.report.ValidateSsmParameterExistsRuleReport;
import com.lyr.rule.ssm.report.ValidateSsmParameterValueRuleReport;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class RuleFactoryTest {

    final RuleSet mockedRuleSet = mock(RuleSet.class);

    @Test
    void testThatRuleFactoryThrowsIllegalArgumentExceptionWhenUnknownRuleNameIsProvided() {
        // Given
        try (MockedStatic<RuleSet> configMockedStatic = Mockito.mockStatic(RuleSet.class)) {
            configMockedStatic.when(RuleSet::getInstance).thenReturn(mockedRuleSet);

            final var unknownRuleName = "unknownRuleName";

            // When & Then
            assertThatThrownBy(() -> RuleFactory.createRule(unknownRuleName))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("allRulesAndExceptedClasses")
    void testThatRuleFactoryCreatesAllAvailableRules(
            final String ruleName,
            final Class<?> expectedTypeOfRule,
            final Class<?> expectedTypeOfRuleImpl,
            final Class<?> expectedTypeOfRuleReport) {
        // Given
        // rulename, expectedTypeOfRule, expectedTypeOfRuleImpl, expectedTypeOfRuleReport
        try (MockedStatic<RuleSet> configMockedStatic = Mockito.mockStatic(RuleSet.class)) {
            configMockedStatic.when(RuleSet::getInstance).thenReturn(mockedRuleSet);

            // When
            final var rule = RuleFactory.createRule(ruleName);

            // Then
            assertThat(rule).isNotNull().isInstanceOf(expectedTypeOfRule);
            assertThat(rule.getRuleExecutionStrategy()).isNotNull().isInstanceOf(expectedTypeOfRuleImpl);
            assertThat(rule.getRuleReportStrategy()).isNotNull().isInstanceOf(expectedTypeOfRuleReport);
        }
    }

    public static Stream<Arguments> allRulesAndExceptedClasses() {
        return Stream.of(
                Arguments.of(
                        SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanRule.class,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl.class,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleReport.class),
                Arguments.of(
                        SCAN_DYNAMODB_TABLE_IDLE,
                        ScanRule.class,
                        ScanDynamodbTableIdleRuleImpl.class,
                        ScanDynamodbTableIdleRuleReport.class),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                        ScanRule.class,
                        ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl.class,
                        ScanLambdaFunctionWithUnboundedConcurrencyRuleReport.class),
                Arguments.of(
                        VALIDATE_LAMBDA_FUNCTION_CONCURRENCY,
                        ValidationRule.class,
                        ValidateLambdaFunctionConcurrencyRuleImpl.class,
                        ValidateLambdaFunctionConcurrencyRuleReport.class),
                Arguments.of(
                        VALIDATE_LAMBDA_FUNCTION_EXISTS,
                        ValidationRule.class,
                        ValidateLambdaFunctionExistsRuleImpl.class,
                        ValidateLambdaFunctionExistsRuleReport.class),
                Arguments.of(
                        VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE,
                        ValidationRule.class,
                        ValidateLambdaFunctionTriggerStateRuleImpl.class,
                        ValidateLambdaFunctionTriggerStateRuleReport.class),
                Arguments.of(
                        VALIDATE_SSM_PARAMETER_EXISTS,
                        ValidationRule.class,
                        ValidateSsmParameterExistsRuleImpl.class,
                        ValidateSsmParameterExistsRuleReport.class),
                Arguments.of(
                        VALIDATE_SSM_PARAMETER_VALUE,
                        ValidationRule.class,
                        ValidateSsmParameterValueRuleImpl.class,
                        ValidateSsmParameterValueRuleReport.class));
    }
}
