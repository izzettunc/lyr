package com.lyr.rule;

import com.lyr.exception.rule.UnknownRuleException;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.util.RuleDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleConfigFactory {

    public static RuleConfig createRuleConfig(final String ruleName, final Object config) {
        final var ruleDefinition = RuleDefinition.definitionByName(ruleName);

        if (ruleDefinition == null) {
            throw new UnknownRuleException("A config for an unknown rule tried to be created. Rule name: " + ruleName);
        }

        return switch (ruleDefinition) {
            case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(config);
            case SCAN_DYNAMODB_TABLE_IDLE -> ScanDynamodbTableIdleRuleConfig.parse(config);
            case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(config);
        };
    }
}
