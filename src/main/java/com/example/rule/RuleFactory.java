package com.example.rule;

import com.example.config.Config;
import com.example.rule.dynamodb.ScanDynamodbTableIdleRuleImpl;
import com.example.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.example.rule.dynamodb.report.ScanDynamodbTableIdleRuleReport;
import com.example.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl;
import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.glue.report.ScanGlueSessionActiveWithLongIdleTimeoutRuleReport;
import com.example.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionConcurrencyRuleImpl;
import com.example.rule.lambda.ValidateLambdaFunctionTriggerStateRuleImpl;
import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.lambda.report.ScanLambdaFunctionWithUnboundedConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaConcurrencyRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionExistsRuleReport;
import com.example.rule.lambda.report.ValidateLambdaFunctionTriggerStateRuleReport;
import com.example.rule.lambda.LambdaReason;
import com.example.rule.lambda.ValidateLambdaFunctionExistsRuleImpl;
import com.example.rule.ssm.ValidateSsmParameterExistsRuleImpl;
import com.example.rule.ssm.ValidateSsmParameterValueRuleImpl;
import com.example.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import com.example.rule.ssm.report.ValidateSsmParameterExistsRuleReport;
import com.example.rule.ssm.report.ValidateSsmParameterValueRuleReport;
import com.example.rule.ssm.SsmReason;

public class RuleFactory {

    public static Rule createRule(String ruleName) {
        var parameters = Config.CONFIG.getRuleConfig().get(ruleName);

        return switch (ruleName) {
            case ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.NAME ->
                    new ScanRule(ruleName, parameters, new ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl(),
                            new ScanGlueSessionActiveWithLongIdleTimeoutRuleReport());
            case ValidateLambdaFunctionExistsRuleConfig.NAME ->
                    new ValidationRule<LambdaReason>(ruleName, parameters, new ValidateLambdaFunctionExistsRuleImpl(),
                            new ValidateLambdaFunctionExistsRuleReport());
            case ValidateLambdaFunctionConcurrencyRuleConfig.NAME ->
                    new ValidationRule<LambdaReason>(ruleName, parameters, new ValidateLambdaFunctionConcurrencyRuleImpl(),
                            new ValidateLambdaConcurrencyRuleReport());
            case ValidateLambdaFunctionTriggerStateRuleConfig.NAME ->
                    new ValidationRule<LambdaReason>(ruleName, parameters, new ValidateLambdaFunctionTriggerStateRuleImpl(),
                            new ValidateLambdaFunctionTriggerStateRuleReport());
            case ScanDynamodbTableIdleRuleConfig.NAME -> new ScanRule(ruleName, parameters, new ScanDynamodbTableIdleRuleImpl(),
                    new ScanDynamodbTableIdleRuleReport());
            case ValidateSsmParameterExistsRuleConfig.NAME ->
                    new ValidationRule<SsmReason>(ruleName, parameters, new ValidateSsmParameterExistsRuleImpl(),
                            new ValidateSsmParameterExistsRuleReport());
            case ValidateSsmParameterValueRuleConfig.NAME ->
                    new ValidationRule<SsmReason>(ruleName, parameters, new ValidateSsmParameterValueRuleImpl(),
                            new ValidateSsmParameterValueRuleReport());
            case ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.NAME ->
                    new ScanRule(ruleName, parameters, new ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl(),
                            new ScanLambdaFunctionWithUnboundedConcurrencyRuleReport());
            default -> throw new IllegalArgumentException("Unknown rule name: " + ruleName);
        };
    }
}
