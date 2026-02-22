package com.example.rule;

import static com.example.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static com.example.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.example.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;

import com.example.config.Config;
import com.example.rule.dynamodb.ScanDynamodbTableIdleRuleImpl;
import com.example.rule.dynamodb.report.ScanDynamodbTableIdleRuleReport;
import com.example.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl;
import com.example.rule.glue.report.ScanGlueSessionActiveWithLongIdleTimeoutRuleReport;
import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionExistsRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionTriggerStateRuleImpl;
import com.example.rule.lambda.report.ScanLambdaFunctionWithUnboundedConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionExistsRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionTriggerStateRuleReport;
import com.example.rule.ssm.SsmReason;
import com.example.rule.ssm.ValidateSsmParameterExistsRuleImpl;
import com.example.rule.ssm.ValidateSsmParameterValueRuleImpl;
import com.example.rule.ssm.report.ValidateSsmParameterExistsRuleReport;
import com.example.rule.ssm.report.ValidateSsmParameterValueRuleReport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleFactory {

    public static Rule createRule(final String ruleName) {
        final var ruleConfig = Config.getConfig().getRuleConfig().get(ruleName);

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
