package com.lyr.rule;

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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleFactory {

    public static Rule createRule(@NonNull final RuleDefinition ruleDefinition) {
        final var ruleExecutionStrategy =
                switch (ruleDefinition) {
                    case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                        new ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution();
                    case SCAN_DYNAMODB_TABLE_IDLE -> new ScanDynamodbTableIdleRuleExecution();
                    case SCAN_DYNAMODB_TABLE_WITHOUT_BACKUP -> new ScanDynamodbTableWithoutBackupRuleExecution();
                    case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                        new ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution();
                    case SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE ->
                        new ScanLambdaFunctionWithDisallowedArchitectureRuleExecution();
                    case SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED ->
                        new ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution();
                    case SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER ->
                        new ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution();
                    case SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY ->
                        new ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution();
                };

        return new Rule(
                ruleDefinition, RuleSetConfig.getRuleSetConfig().getRuleConfig(ruleDefinition), ruleExecutionStrategy);
    }
}
