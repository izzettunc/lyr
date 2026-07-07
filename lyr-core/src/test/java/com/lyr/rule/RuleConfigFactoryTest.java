package com.lyr.rule;

import static com.lyr.util.RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfigCreator;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator;
import com.lyr.util.RuleDefinition;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

class RuleConfigFactoryTest {

    static MockedStatic<RuleConfigFactory> mockedRuleConfigFactory =
            mockStatic(RuleConfigFactory.class, CALLS_REAL_METHODS);
    static ScanDynamodbTableIdleRuleConfigCreator mockedScanDynamodbTableIdleRuleConfigCreator =
            mock(ScanDynamodbTableIdleRuleConfigCreator.class);
    static ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator
            mockedScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator =
                    mock(ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator.class);
    static ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator
            mockedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator =
                    mock(ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator.class);
    static ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator
            mockedScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator =
                    mock(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator.class);
    static ScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator
            mockedScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator =
                    mock(ScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator.class);

    @BeforeEach
    void beforeEach() {
        mockedRuleConfigFactory.reset();
    }

    @AfterAll
    static void beforeAll() {
        mockedRuleConfigFactory.close();
    }

    @ParameterizedTest
    @MethodSource("ruleDefinitionAndRespectiveRuleConfigCreator")
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsCreatedCorrectly(
            final RuleDefinition ruleDefinition, final RuleConfigCreator<? extends RuleConfig> ruleConfigCreator) {
        // Given
        final Object input = null;

        // When
        when(RuleConfigFactory.getRuleConfigCreatorMap()).thenReturn(Map.of(ruleDefinition, ruleConfigCreator));
        RuleConfigFactory.createRuleConfig(ruleDefinition, input);

        // Then
        verify(ruleConfigCreator, times(1)).create(input);
    }

    public static Stream<Arguments> ruleDefinitionAndRespectiveRuleConfigCreator() {
        return Stream.of(
                Arguments.of(SCAN_DYNAMODB_TABLE_IDLE, mockedScanDynamodbTableIdleRuleConfigCreator),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                        mockedScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator),
                Arguments.of(
                        SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE,
                        mockedScanLambdaFunctionWithDisallowedArchitectureRuleConfigCreator),
                Arguments.of(
                        SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        mockedScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator),
                Arguments.of(
                        SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                        mockedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator));
    }
}
