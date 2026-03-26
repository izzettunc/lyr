package com.lyr.rule;

import static com.lyr.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;

import com.lyr.config.RuleSetConfig;
import com.lyr.rule.dynamodb.ScanDynamodbTableIdleRuleImpl;
import com.lyr.rule.dynamodb.report.ScanDynamodbTableIdleRuleReport;
import com.lyr.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl;
import com.lyr.rule.glue.report.ScanGlueSessionActiveWithLongIdleTimeoutRuleReport;
import com.lyr.rule.lambda.LambdaReason;
import com.lyr.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl;
import com.lyr.rule.lambda.ValidateLambdaFunctionConcurrencyRuleImpl;
import com.lyr.rule.lambda.ValidateLambdaFunctionExistsRuleImpl;
import com.lyr.rule.lambda.ValidateLambdaFunctionTriggerStateRuleImpl;
import com.lyr.rule.lambda.report.ScanLambdaFunctionWithUnboundedConcurrencyRuleReport;
import com.lyr.rule.lambda.report.ValidateLambdaFunctionConcurrencyRuleReport;
import com.lyr.rule.lambda.report.ValidateLambdaFunctionExistsRuleReport;
import com.lyr.rule.lambda.report.ValidateLambdaFunctionTriggerStateRuleReport;
import com.lyr.rule.ssm.SsmReason;
import com.lyr.rule.ssm.ValidateSsmParameterExistsRuleImpl;
import com.lyr.rule.ssm.ValidateSsmParameterValueRuleImpl;
import com.lyr.rule.ssm.report.ValidateSsmParameterExistsRuleReport;
import com.lyr.rule.ssm.report.ValidateSsmParameterValueRuleReport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleFactory {

    public static Rule createRule(final String ruleName) {
        final var ruleConfig =
                RuleSetConfig.getInstance().getRuleToRuleConfigMap().get(ruleName);

        return switch (ruleName) {
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                new ScanRule(
                        ruleName,
                        ruleConfig,
                        new ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl(),
                        new ScanGlueSessionActiveWithLongIdleTimeoutRuleReport());
            case SCAN_DYNAMODB_TABLE_IDLE ->
                new ScanRule(
                        ruleName,
                        ruleConfig,
                        new ScanDynamodbTableIdleRuleImpl(),
                        new ScanDynamodbTableIdleRuleReport());
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                new ScanRule(
                        ruleName,
                        ruleConfig,
                        new ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl(),
                        new ScanLambdaFunctionWithUnboundedConcurrencyRuleReport());
            case VALIDATE_LAMBDA_FUNCTION_EXISTS ->
                new ValidationRule<LambdaReason>(
                        ruleName,
                        ruleConfig,
                        new ValidateLambdaFunctionExistsRuleImpl(),
                        new ValidateLambdaFunctionExistsRuleReport());
            case VALIDATE_LAMBDA_FUNCTION_CONCURRENCY ->
                new ValidationRule<LambdaReason>(
                        ruleName,
                        ruleConfig,
                        new ValidateLambdaFunctionConcurrencyRuleImpl(),
                        new ValidateLambdaFunctionConcurrencyRuleReport());
            case VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE ->
                new ValidationRule<LambdaReason>(
                        ruleName,
                        ruleConfig,
                        new ValidateLambdaFunctionTriggerStateRuleImpl(),
                        new ValidateLambdaFunctionTriggerStateRuleReport());
            case VALIDATE_SSM_PARAMETER_EXISTS ->
                new ValidationRule<SsmReason>(
                        ruleName,
                        ruleConfig,
                        new ValidateSsmParameterExistsRuleImpl(),
                        new ValidateSsmParameterExistsRuleReport());
            case VALIDATE_SSM_PARAMETER_VALUE ->
                new ValidationRule<SsmReason>(
                        ruleName,
                        ruleConfig,
                        new ValidateSsmParameterValueRuleImpl(),
                        new ValidateSsmParameterValueRuleReport());
            default -> throw new IllegalArgumentException("Unknown rule name: " + ruleName);
        };
    }
}
