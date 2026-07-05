package com.lyr.rule;

import static com.lyr.util.RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;

import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfigCreator;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator;
import com.lyr.util.RuleDefinition;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleConfigFactory {

    private static final Map<RuleDefinition, RuleConfigCreator<? extends RuleConfig>> RULE_CONFIG_CREATOR_MAP = Map.of(
            SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                    new ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator(),
            SCAN_DYNAMODB_TABLE_IDLE, new ScanDynamodbTableIdleRuleConfigCreator(),
            SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                    new ScanGlueSessionActiveWithLongIdleTimeoutRuleConfigCreator(),
            SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                    new ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator());

    static Map<RuleDefinition, RuleConfigCreator<? extends RuleConfig>> getRuleConfigCreatorMap() {
        return RULE_CONFIG_CREATOR_MAP;
    }

    public static RuleConfig createRuleConfig(final RuleDefinition ruleDefinition, final Object config) {
        return switch (ruleDefinition) {
            case SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                    SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                    SCAN_DYNAMODB_TABLE_IDLE,
                    SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                getRuleConfigCreatorMap().get(ruleDefinition).create(config);
        };
    }
}
