package com.lyr.rule;

import com.lyr.config.RuleSetConfig;
import com.lyr.rule.cloudwatch.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution;
import com.lyr.rule.dynamodb.ScanDynamodbTableIdleRuleExecution;
import com.lyr.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithDisallowedArchitectureRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution;
import com.lyr.util.RuleDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleFactory {

    public static Rule createRule(final RuleDefinition ruleDefinition) {
        return switch (ruleDefinition) {
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                new Rule(
                        ruleDefinition,
                        RuleSetConfig.getRuleSetConfig().getConfig(ruleDefinition),
                        new ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution());
            case SCAN_DYNAMODB_TABLE_IDLE ->
                new Rule(
                        ruleDefinition,
                        RuleSetConfig.getRuleSetConfig().getConfig(ruleDefinition),
                        new ScanDynamodbTableIdleRuleExecution());
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                new Rule(
                        ruleDefinition,
                        RuleSetConfig.getRuleSetConfig().getConfig(ruleDefinition),
                        new ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution());
            case SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE ->
                new Rule(
                        ruleDefinition,
                        RuleSetConfig.getRuleSetConfig().getConfig(ruleDefinition),
                        new ScanLambdaFunctionWithDisallowedArchitectureRuleExecution());
            case SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY ->
                new Rule(
                        ruleDefinition,
                        RuleSetConfig.getRuleSetConfig().getConfig(ruleDefinition),
                        new ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution());
        };
    }
}
