package com.lyr.rule;

import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.util.RuleDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleConfigFactory {

    public static RuleConfig createRuleConfig(final RuleDefinition ruleDefinition, final Object config) {
        return switch (ruleDefinition) {
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config);
            case SCAN_DYNAMODB_TABLE_IDLE -> ScanDynamodbTableIdleRuleConfig.parse(config);
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(config);
            case SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY ->
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.parse(config);
        };
    }
}
