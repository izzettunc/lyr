package com.lyr.rule;

import static com.lyr.util.RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_WITHOUT_BACKUP;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_WITHOUT_DELETION_PROTECTION;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.lyr.config.RuleSetConfig;
import com.lyr.rule.cloudwatch.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution;
import com.lyr.rule.dynamodb.ScanDynamodbTableIdleRuleExecution;
import com.lyr.rule.dynamodb.ScanDynamodbTableWithoutBackupRuleExecution;
import com.lyr.rule.dynamodb.ScanDynamodbTableWithoutDeletionProtectionRuleExecution;
import com.lyr.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithDisallowedArchitectureRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution;
import com.lyr.util.RuleDefinition;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class RuleFactoryTest {

    static MockedStatic<RuleSetConfig> mockedRuleSetConfig = Mockito.mockStatic(RuleSetConfig.class);
    final RuleSetConfig mockedRuleSetConfigInstance = mock(RuleSetConfig.class);
    final RuleConfig mockedRuleConfig = mock(RuleConfig.class);

    @BeforeEach
    void beforeEach() {
        mockedRuleSetConfig.when(RuleSetConfig::getRuleSetConfig).thenReturn(mockedRuleSetConfigInstance);
        when(mockedRuleSetConfigInstance.getRuleConfig(any())).thenReturn(mockedRuleConfig);
    }

    @AfterEach
    void afterEach() {
        reset(mockedRuleSetConfigInstance, mockedRuleConfig);
        mockedRuleSetConfig.reset();
    }

    @AfterAll
    static void afterAll() {
        mockedRuleSetConfig.closeOnDemand();
    }

    @ParameterizedTest
    @MethodSource("allRulesAndExceptedClasses")
    void testThatRuleFactoryCreatesRulesCorrectly(
            final RuleDefinition ruleDefinition, final Class<?> expectedTypeOfRuleExecution) {
        // Given ruleDefinition, expectedTypeOfRuleExecution
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

    @Test
    void testThatRuleFactoryThrowsUnknownRuleExceptionWhenInvalidRuleDefinitionIsProvided() {
        // Given
        final RuleDefinition nullRuleDefinition = null;

        // When & Then
        assertThatThrownBy(() -> RuleFactory.createRule(nullRuleDefinition)).isInstanceOf(NullPointerException.class);
    }

    public static Stream<Arguments> allRulesAndExceptedClasses() {
        final var ruleDefinitionToExecutionMap = Map.ofEntries(
                Map.entry(
                        SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution.class),
                Map.entry(SCAN_DYNAMODB_TABLE_IDLE, ScanDynamodbTableIdleRuleExecution.class),
                Map.entry(SCAN_DYNAMODB_TABLE_WITHOUT_BACKUP, ScanDynamodbTableWithoutBackupRuleExecution.class),
                Map.entry(
                        SCAN_DYNAMODB_TABLE_WITHOUT_DELETION_PROTECTION,
                        ScanDynamodbTableWithoutDeletionProtectionRuleExecution.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                        ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE,
                        ScanLambdaFunctionWithDisallowedArchitectureRuleExecution.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED,
                        ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER,
                        ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution.class),
                Map.entry(
                        SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                        ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution.class));

        return Arrays.stream(RuleDefinition.values())
                .map(ruleDefinition -> Arguments.of(ruleDefinition, ruleDefinitionToExecutionMap.get(ruleDefinition)));
    }
}
