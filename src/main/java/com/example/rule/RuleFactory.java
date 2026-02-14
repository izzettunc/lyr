package com.example.rule;

import com.example.config.Config;
import com.example.rule.dynamodb.ScanDynamodbTableIdleRuleImpl;
import com.example.rule.dynamodb.report.ScanDynamodbTableIdleRuleReport;
import com.example.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl;
import com.example.rule.glue.report.ScanGlueSessionActiveWithLongIdleTimeoutRuleReport;
import com.example.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionTriggerStateRuleImpl;
import com.example.rule.lambda.report.ScanLambdaFunctionWithUnboundedConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionExistsRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionTriggerStateRuleReport;
import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.ValidateLambdaFunctionExistsRuleImpl;
import com.example.rule.ssm.ValidateSsmParameterExistsRuleImpl;
import com.example.rule.ssm.ValidateSsmParameterValueRuleImpl;
import com.example.rule.ssm.report.ValidateSsmParameterExistsRuleReport;
import com.example.rule.ssm.report.ValidateSsmParameterValueRuleReport;
import com.example.rule.ssm.SsmReason;

import static com.example.rule.Constants.*;

public class RuleFactory {
    private RuleFactory() {
    }

    public static Rule createRule(String ruleName) {
        var parameters = Config.getConfig().getRuleConfig().get(ruleName);

        return switch (ruleName) {
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                    new ScanRule(ruleName, parameters, new ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl(),
                            new ScanGlueSessionActiveWithLongIdleTimeoutRuleReport());
            case SCAN_DYNAMODB_TABLE_IDLE -> new ScanRule(ruleName, parameters, new ScanDynamodbTableIdleRuleImpl(),
                    new ScanDynamodbTableIdleRuleReport());
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                    new ScanRule(ruleName, parameters, new ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl(),
                            new ScanLambdaFunctionWithUnboundedConcurrencyRuleReport());
            case VALIDATE_LAMBDA_FUNCTION_EXISTS ->
                    new ValidationRule<LambdaReason>(ruleName, parameters, new ValidateLambdaFunctionExistsRuleImpl(),
                            new ValidateLambdaFunctionExistsRuleReport());
            case VALIDATE_LAMBDA_FUNCTION_CONCURRENCY ->
                    new ValidationRule<LambdaReason>(ruleName, parameters, new ValidateLambdaFunctionConcurrencyRuleImpl(),
                            new ValidateLambdaConcurrencyRuleReport());
            case VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE ->
                    new ValidationRule<LambdaReason>(ruleName, parameters, new ValidateLambdaFunctionTriggerStateRuleImpl(),
                            new ValidateLambdaFunctionTriggerStateRuleReport());
            case VALIDATE_SSM_PARAMETER_EXISTS ->
                    new ValidationRule<SsmReason>(ruleName, parameters, new ValidateSsmParameterExistsRuleImpl(),
                            new ValidateSsmParameterExistsRuleReport());
            case VALIDATE_SSM_PARAMETER_VALUE ->
                    new ValidationRule<SsmReason>(ruleName, parameters, new ValidateSsmParameterValueRuleImpl(),
                            new ValidateSsmParameterValueRuleReport());
            default -> throw new IllegalArgumentException("Unknown rule name: " + ruleName);
        };
    }
}
