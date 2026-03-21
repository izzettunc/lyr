package com.lyr.rule;

import static com.lyr.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;

import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.lyr.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.lyr.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleConfigFactory {

    public static RuleConfig createRuleConfig(final String ruleName, final Object config) {
        return switch (ruleName) {
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config);
            case SCAN_DYNAMODB_TABLE_IDLE -> ScanDynamodbTableIdleRuleConfig.parse(config);
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(config);
            case VALIDATE_LAMBDA_FUNCTION_EXISTS -> ValidateLambdaFunctionExistsRuleConfig.parse(config);
            case VALIDATE_LAMBDA_FUNCTION_CONCURRENCY -> ValidateLambdaFunctionConcurrencyRuleConfig.parse(config);
            case VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(config);
            case VALIDATE_SSM_PARAMETER_EXISTS -> ValidateSsmParameterExistsRuleConfig.parse(config);
            case VALIDATE_SSM_PARAMETER_VALUE -> ValidateSsmParameterValueRuleConfig.parse(config);
            default -> throw new IllegalArgumentException("Unknown rule name: " + ruleName);
        };
    }
}
