package com.example.rule;

import static com.example.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static com.example.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.example.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.example.config.Config;
import com.example.rule.dynamodb.ScanDynamodbTableIdleRuleImpl;
import com.example.rule.dynamodb.report.ScanDynamodbTableIdleRuleReport;
import com.example.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl;
import com.example.rule.glue.report.ScanGlueSessionActiveWithLongIdleTimeoutRuleReport;
import com.example.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionExistsRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionTriggerStateRuleImpl;
import com.example.rule.lambda.report.ScanLambdaFunctionWithUnboundedConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionExistsRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionTriggerStateRuleReport;
import com.example.rule.ssm.ValidateSsmParameterExistsRuleImpl;
import com.example.rule.ssm.ValidateSsmParameterValueRuleImpl;
import com.example.rule.ssm.report.ValidateSsmParameterExistsRuleReport;
import com.example.rule.ssm.report.ValidateSsmParameterValueRuleReport;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class RuleFactoryTest {

    final Config mockedConfig = mock(Config.class);

    @Test
    void testThatRuleFactoryThrowsIllegalArgumentExceptionWhenUnknownRuleNameIsProvided() {
        // Given
        try (MockedStatic<Config> configMockedStatic = Mockito.mockStatic(Config.class)) {
            configMockedStatic.when(Config::getConfig).thenReturn(mockedConfig);

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
        try (MockedStatic<Config> configMockedStatic = Mockito.mockStatic(Config.class)) {
            configMockedStatic.when(Config::getConfig).thenReturn(mockedConfig);

            // When
            final var rule = RuleFactory.createRule(ruleName);

            // Then
            assertThat(rule).isNotNull().isInstanceOf(expectedTypeOfRule);
            assertThat(rule.getStrategy()).isNotNull().isInstanceOf(expectedTypeOfRuleImpl);
            assertThat(rule.getReport()).isNotNull().isInstanceOf(expectedTypeOfRuleReport);
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
