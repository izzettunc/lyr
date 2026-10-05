package com.lyr.report.style.finding;

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

import com.lyr.report.style.finding.cloudwatch.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleFindingStyler;
import com.lyr.report.style.finding.dynamodb.ScanDynamodbTableIdleRuleFindingStyler;
import com.lyr.report.style.finding.dynamodb.ScanDynamodbTableWithoutBackupRuleFindingStyler;
import com.lyr.report.style.finding.dynamodb.ScanDynamodbTableWithoutDeletionProtectionRuleFindingStyler;
import com.lyr.report.style.finding.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithDisallowedArchitectureRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithoutAnyActiveTriggerRuleFindingStyler;
import com.lyr.util.RuleDefinition;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StylerFactoryTest {

    @ParameterizedTest
    @MethodSource("allRulesAndExceptedStylerClasses")
    void testThatCorrectStylerCreatedSuccessfullyForGivenRuleDefinition(
            final RuleDefinition ruleDefinition, final Class<?> expectedStylerClass) {
        // Given ruleDefinition and expectedStylerClass

        // When
        final var actualStyler = StylerFactory.getStylerFor(ruleDefinition);

        // Then
        assertThat(actualStyler).isInstanceOf(expectedStylerClass);
    }

    @Test
    void testThatStylerFactoryThrowsUnknownRuleExceptionWhenInvalidRuleDefinitionIsProvided() {
        // Given
        final RuleDefinition nullRuleDefinition = null;

        // When & Then
        assertThatThrownBy(() -> StylerFactory.getStylerFor(nullRuleDefinition))
                .isInstanceOf(NullPointerException.class);
    }

    public static Stream<Arguments> allRulesAndExceptedStylerClasses() {
        final var ruleDefinitionToStylerMap = Map.ofEntries(
                Map.entry(
                        SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler.class),
                Map.entry(SCAN_DYNAMODB_TABLE_IDLE, ScanDynamodbTableIdleRuleFindingStyler.class),
                Map.entry(SCAN_DYNAMODB_TABLE_WITHOUT_BACKUP, ScanDynamodbTableWithoutBackupRuleFindingStyler.class),
                Map.entry(
                        SCAN_DYNAMODB_TABLE_WITHOUT_DELETION_PROTECTION,
                        ScanDynamodbTableWithoutDeletionProtectionRuleFindingStyler.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                        ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE,
                        ScanLambdaFunctionWithDisallowedArchitectureRuleFindingStyler.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED,
                        ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStyler.class),
                Map.entry(
                        SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER,
                        ScanLambdaFunctionWithoutAnyActiveTriggerRuleFindingStyler.class),
                Map.entry(
                        SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                        ScanCloudwatchLogGroupWithoutRetentionPolicyRuleFindingStyler.class));

        return Arrays.stream(RuleDefinition.values())
                .map(ruleDefinition -> Arguments.of(ruleDefinition, ruleDefinitionToStylerMap.get(ruleDefinition)));
    }
}
