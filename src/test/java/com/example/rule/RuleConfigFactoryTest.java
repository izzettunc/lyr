package com.example.rule;

import com.example.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;


import static com.example.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static com.example.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.example.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_CONCURRENCY;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;
import static com.example.rule.Constants.VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE;
import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

public class RuleConfigFactoryTest {

    @Test
    void testThatRuleFactoryThrowsIllegalArgumentExceptionWhenUnknownRuleNameIsProvided() {
        // Given
        var unknownRuleName = "unknownRuleName";

        // When & Then
        assertThatThrownBy(() -> RuleConfigFactory.createRuleConfig(unknownRuleName, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> mockedStaticConfig = mockStatic(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, null);

            mockedStaticConfig.verify(() -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ScanDynamodbTableIdleRuleConfig> mockedStaticConfig = mockStatic(ScanDynamodbTableIdleRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(SCAN_DYNAMODB_TABLE_IDLE, null);

            mockedStaticConfig.verify(() -> ScanDynamodbTableIdleRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateSsmParameterExistsRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ValidateSsmParameterExistsRuleConfig> mockedStaticConfig = mockStatic(ValidateSsmParameterExistsRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_SSM_PARAMETER_EXISTS, null);

            mockedStaticConfig.verify(() -> ValidateSsmParameterExistsRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateSsmParameterValueRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ValidateSsmParameterValueRuleConfig> mockedStaticConfig = mockStatic(ValidateSsmParameterValueRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_SSM_PARAMETER_VALUE, null);

            mockedStaticConfig.verify(() -> ValidateSsmParameterValueRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateLambdaFunctionExistsRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ValidateLambdaFunctionExistsRuleConfig> mockedStaticConfig = mockStatic(ValidateLambdaFunctionExistsRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_LAMBDA_FUNCTION_EXISTS, null);

            mockedStaticConfig.verify(() -> ValidateLambdaFunctionExistsRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateLambdaFunctionConcurrencyRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ValidateLambdaFunctionConcurrencyRuleConfig> mockedStaticConfig = mockStatic(ValidateLambdaFunctionConcurrencyRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY, null);

            mockedStaticConfig.verify(() -> ValidateLambdaFunctionConcurrencyRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> mockedStaticConfig = mockStatic(ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY, null);

            mockedStaticConfig.verify(() -> ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(any()), times(1));
        }
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleConfigIsCreatedCorrectly() {
        try(MockedStatic<ValidateLambdaFunctionTriggerStateRuleConfig> mockedStaticConfig = mockStatic(ValidateLambdaFunctionTriggerStateRuleConfig.class)) {
            RuleConfigFactory.createRuleConfig(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE, null);

            mockedStaticConfig.verify(() -> ValidateLambdaFunctionTriggerStateRuleConfig.parse(any()), times(1));
        }
    }


//    public static Stream<Arguments> allRulesAndExceptedClasses() {
//        return Stream.of(
//                Arguments.of(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, ScanRule.class, ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl.class, ScanGlueSessionActiveWithLongIdleTimeoutRuleReport.class),
//                Arguments.of(SCAN_DYNAMODB_TABLE_IDLE, ScanRule.class, ScanDynamodbTableIdleRuleImpl.class, ScanDynamodbTableIdleRuleReport.class),
//                Arguments.of(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY, ScanRule.class, ScanLambdaFunctionWithUnboundedConcurrencyRuleImpl.class, ScanLambdaFunctionWithUnboundedConcurrencyRuleReport.class),
//                Arguments.of(VALIDATE_LAMBDA_FUNCTION_CONCURRENCY, ValidationRule.class, ValidateLambdaFunctionConcurrencyRuleImpl.class, ValidateLambdaConcurrencyRuleReport.class),
//                Arguments.of(VALIDATE_LAMBDA_FUNCTION_EXISTS, ValidationRule.class, ValidateLambdaFunctionExistsRuleImpl.class, ValidateLambdaFunctionExistsRuleReport.class),
//                Arguments.of(VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE, ValidationRule.class, ValidateLambdaFunctionTriggerStateRuleImpl.class, ValidateLambdaFunctionTriggerStateRuleReport.class),
//                Arguments.of(VALIDATE_SSM_PARAMETER_EXISTS, ValidationRule.class, ValidateSsmParameterExistsRuleImpl.class, ValidateSsmParameterExistsRuleReport.class),
//                Arguments.of(VALIDATE_SSM_PARAMETER_VALUE, ValidationRule.class, ValidateSsmParameterValueRuleImpl.class, ValidateSsmParameterValueRuleReport.class)
//        );
//    }
}
