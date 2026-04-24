package com.lyr.rule;

import com.lyr.config.RuleSetConfig;
import com.lyr.exception.rule.UnknownRuleException;
import com.lyr.rule.dynamodb.ScanDynamodbTableIdleRuleExecution;
import com.lyr.rule.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution;
import com.lyr.rule.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution;
import com.lyr.util.RuleDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleFactory {

    public static Rule createRule(final String ruleName) {
        final var ruleConfig =
                RuleSetConfig.getInstance().getRuleToRuleConfigMap().get(ruleName);
        final var ruleDefinition = RuleDefinition.definitionByName(ruleName);

        if (ruleDefinition == null) {
            throw new UnknownRuleException("An unknown rule tried to be created. Rule name: " + ruleName);
        }

        return switch (ruleDefinition) {
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                new Rule(ruleDefinition, ruleConfig, new ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution());
            case SCAN_DYNAMODB_TABLE_IDLE ->
                new Rule(ruleDefinition, ruleConfig, new ScanDynamodbTableIdleRuleExecution());
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                new Rule(ruleDefinition, ruleConfig, new ScanLambdaFunctionWithUnboundedConcurrencyRuleExecution());
        };
    }
}
