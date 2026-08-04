package com.lyr.report.style.finding;

import com.lyr.report.style.finding.cloudwatch.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleFindingStyler;
import com.lyr.report.style.finding.dynamodb.ScanDynamodbTableIdleRuleFindingStyler;
import com.lyr.report.style.finding.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithDisallowedArchitectureRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithoutAnyActiveTriggerRuleFindingStyler;
import com.lyr.util.RuleDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StylerFactory {
    public static FindingStyler getStylerFor(final RuleDefinition ruleDefinition) {
        return switch (ruleDefinition) {
            case SCAN_DYNAMODB_TABLE_IDLE -> new ScanDynamodbTableIdleRuleFindingStyler();
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                new ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler();
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                new ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler();
            case SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY ->
                new ScanCloudwatchLogGroupWithoutRetentionPolicyRuleFindingStyler();
            case SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE ->
                new ScanLambdaFunctionWithDisallowedArchitectureRuleFindingStyler();
            case SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED ->
                new ScanLambdaFunctionWithXrayTracingNotEnabledRuleFindingStyler();
            case SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER ->
                new ScanLambdaFunctionWithoutAnyActiveTriggerRuleFindingStyler();
        };
    }
}
