package com.lyr.rule;

import static com.lyr.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.lyr.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.lyr.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.lyr.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RuleConfigFactoryTest {

    @Test
    void testThatRuleFactoryThrowsIllegalArgumentExceptionWhenUnknownRuleNameIsProvided() {
        // Given
        final var unknownRuleName = "unknownRuleName";

        // When & Then
        assertThatThrownBy(() -> RuleConfigFactory.createRuleConfig(unknownRuleName, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

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
    void testThatValidateSsmParameterExistsRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ValidateSsmParameterExistsRuleConfig> mockedStaticConfig =
                mockStatic(ValidateSsmParameterExistsRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_SSM_PARAMETER_EXISTS, null);

            mockedStaticConfig.verify(() -> ValidateSsmParameterExistsRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateSsmParameterValueRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ValidateSsmParameterValueRuleConfig> mockedStaticConfig =
                mockStatic(ValidateSsmParameterValueRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_SSM_PARAMETER_VALUE, null);

            mockedStaticConfig.verify(() -> ValidateSsmParameterValueRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateLambdaFunctionExistsRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ValidateLambdaFunctionExistsRuleConfig> mockedStaticConfig =
                mockStatic(ValidateLambdaFunctionExistsRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_LAMBDA_FUNCTION_EXISTS, null);

            mockedStaticConfig.verify(() -> ValidateLambdaFunctionExistsRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ValidateLambdaFunctionConcurrencyRuleConfig> mockedStaticConfig =
                mockStatic(ValidateLambdaFunctionConcurrencyRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY, null);

            mockedStaticConfig.verify(() -> ValidateLambdaFunctionConcurrencyRuleConfig.parse(any()), times(1));
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

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleConfigIsCreatedCorrectly() {
        try (MockedStatic<ValidateLambdaFunctionTriggerStateRuleConfig> mockedStaticConfig =
                mockStatic(ValidateLambdaFunctionTriggerStateRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE, null);

            mockedStaticConfig.verify(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(any()), times(1));
        }
    }
}
