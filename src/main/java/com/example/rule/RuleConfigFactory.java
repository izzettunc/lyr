package com.example.rule;

import com.example.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;

public class RuleConfigFactory {
    private RuleConfigFactory() {
    }

    public static RuleConfig createRuleConfig(String ruleName, Object config) {
        return switch (ruleName) {
            case ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.NAME ->
                    ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config);
            case ValidateLambdaFunctionExistsRuleConfig.NAME -> ValidateLambdaFunctionExistsRuleConfig.parse(config);
            case ValidateLambdaFunctionConcurrencyRuleConfig.NAME -> ValidateLambdaFunctionConcurrencyRuleConfig.parse(config);
            case ValidateLambdaFunctionTriggerStateRuleConfig.NAME -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(config);
            case ScanDynamodbTableIdleRuleConfig.NAME -> ScanDynamodbTableIdleRuleConfig.parse(config);
            case ValidateSsmParameterExistsRuleConfig.NAME -> ValidateSsmParameterExistsRuleConfig.parse(config);
            case ValidateSsmParameterValueRuleConfig.NAME ->  ValidateSsmParameterValueRuleConfig.parse(config);
            case ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.NAME -> ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(config);
            default -> throw new IllegalArgumentException("Unknown rule name: " + ruleName);
        };
    }
}
