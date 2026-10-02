package com.lyr.rule;

import static com.lyr.util.RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_WITHOUT_BACKUP;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.lyr.config.RuleSetConfig;
import com.lyr.rule.cloudwatch.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution;
import com.lyr.rule.dynamodb.ScanDynamodbTableIdleRuleExecution;
import com.lyr.rule.dynamodb.ScanDynamodbTableWithoutBackupRuleExecution;
import com.lyr.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithDisallowedArchitectureRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution;
import com.lyr.util.RuleDefinition;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class RuleFactoryTest {

    final RuleSetConfig mockedRuleSetConfig = mock(RuleSetConfig.class);
    final RuleConfig mockedRuleConfig = mock(RuleConfig.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedRuleSetConfig, mockedRuleConfig);
    }

    @ParameterizedTest
    @MethodSource("allRulesAndExceptedClasses")
    void testThatRuleFactoryCreatesAllAvailableRules(
            final RuleDefinition ruleDefinition, final Class<?> expectedTypeOfRuleExecution) {
        // Given
        // ruleDefinition, expectedTypeOfRuleExecution
        try (MockedStatic<RuleSetConfig> configMockedStatic = Mockito.mockStatic(RuleSetConfig.class)) {
            configMockedStatic.when(RuleSetConfig::getRuleSetConfig).thenReturn(mockedRuleSetConfig);
            when(mockedRuleSetConfig.getRuleConfig(ruleDefinition)).thenReturn(mockedRuleConfig);

            // When
            final var rule = RuleFactory.createRule(ruleDefinition);

            // Then
            assertThat(rule).isNotNull().isInstanceOf(Rule.class);
            assertThat(rule)
                    .extracting(Rule::getRuleExecutionStrategy)
                    .isNotNull()
                    .isInstanceOf(expectedTypeOfRuleExecution);
            assertThat(rule).extracting(Rule::getRuleConfig).isNotNull().isEqualTo(mockedRuleConfig);
        }
    }

    @Test
    void testThatRuleFactoryThrowsUnknownRuleExceptionWhenInvalidRuleDefinitionIsProvided() {
        // Given
        final RuleDefinition nullRuleDefinition = null;

        // When & Then
        assertThatThrownBy(() -> RuleFactory.createRule(nullRuleDefinition)).isInstanceOf(NullPointerException.class);
    }

    public static Stream<Arguments> allRulesAndExceptedClasses() {
        return Stream.of(
                Arguments.of(
                        SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution.class),
                Arguments.of(SCAN_DYNAMODB_TABLE_IDLE, ScanDynamodbTableIdleRuleExecution.class),
                Arguments.of(SCAN_DYNAMODB_TABLE_WITHOUT_BACKUP, ScanDynamodbTableWithoutBackupRuleExecution.class),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                        ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution.class),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE,
                        ScanLambdaFunctionWithDisallowedArchitectureRuleExecution.class),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED,
                        ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution.class),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER,
                        ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution.class),
                Arguments.of(
                        SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                        ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution.class));
    }
}
