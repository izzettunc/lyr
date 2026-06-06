package com.lyr.rule;

import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RuleConfigFactoryTest {

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> mockedStaticConfig =
                mockStatic(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, null);

            mockedStaticConfig.verify(() -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ScanDynamodbTableIdleRuleConfig> mockedStaticConfig =
                mockStatic(ScanDynamodbTableIdleRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(SCAN_DYNAMODB_TABLE_IDLE, null);

            mockedStaticConfig.verify(() -> ScanDynamodbTableIdleRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> mockedStaticConfig =
                mockStatic(ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY, null);

            mockedStaticConfig.verify(
                    () -> ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(any()), times(1));
        }
    }
}
