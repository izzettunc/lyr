package com.lyr.config.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.util.RuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuleSetTest {

    RuleSet testObject;

    @BeforeEach
    void beforeEach() {
        testObject = new RuleSet();
    }

    @Test
    void testThatRuleSetSettersBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleSet = new RuleSet();
        expectedRuleSet.setScanDynamodbTableIdleRuleConfig(
                ScanDynamodbTableIdleRuleConfig.builder().build());
        expectedRuleSet.setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build());
        expectedRuleSet.setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build());
        expectedRuleSet.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build());
        expectedRuleSet.setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build());

        // When
        testObject.setScanDynamodbTableIdleRuleConfig(null);
        testObject.setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(null);
        testObject.setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(null);
        testObject.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(null);
        testObject.setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(null);

        // Then
        assertThat(testObject).usingRecursiveComparison().isEqualTo(expectedRuleSet);
    }

    @Test
    void testThatRuleSetSettersUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedScanDynamodbTableIdleRuleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(9999)
                .build();
        final var expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        final var expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                        .disallowedArchitecture("random")
                        .build();
        final var expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(999)
                        .build();
        final var expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        // When
        testObject.setScanDynamodbTableIdleRuleConfig(expectedScanDynamodbTableIdleRuleConfig);
        testObject.setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(
                expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig);
        testObject.setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(
                expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig);
        testObject.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
                expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig);
        testObject.setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(
                expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig);

        // Then
        assertThat(testObject.scanDynamodbTableIdleRuleConfig).isSameAs(expectedScanDynamodbTableIdleRuleConfig);
        assertThat(testObject.scanLambdaFunctionWithUnboundedConcurrencyRuleConfig)
                .isSameAs(expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig);
        assertThat(testObject.scanLambdaFunctionWithDisallowedArchitectureRuleConfig)
                .isSameAs(expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig);
        assertThat(testObject.scanGlueSessionActiveWithLongIdleTimeoutRuleConfig)
                .isSameAs(expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig);
        assertThat(testObject.scanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig)
                .isSameAs(expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig);
    }

    @Test
    void testThatRuleSetGetRuleConfigReturnsCopyOfCorrectRuleConfig() {
        // Given
        final var expectedScanDynamodbTableIdleRuleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(9999)
                .build();
        final var expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        final var expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                        .disallowedArchitecture("random")
                        .build();
        final var expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(999)
                        .build();
        final var expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();

        testObject.setScanDynamodbTableIdleRuleConfig(expectedScanDynamodbTableIdleRuleConfig);
        testObject.setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(
                expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig);
        testObject.setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(
                expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig);
        testObject.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
                expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig);
        testObject.setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(
                expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig);

        // When
        final var actualScanDynamodbTableIdleRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE);
        final var actualScanLambdaFunctionWithUnboundedConcurrencyRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY);
        final var actualScanLambdaFunctionWithDisallowedArchitectureRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE);
        final var actualScanGlueSessionActiveWithLongIdleTimeoutRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
        final var actualScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY);

        // Then
        assertThat(actualScanDynamodbTableIdleRuleConfig)
                .isNotSameAs(expectedScanDynamodbTableIdleRuleConfig)
                .isEqualTo(expectedScanDynamodbTableIdleRuleConfig);

        assertThat(actualScanLambdaFunctionWithUnboundedConcurrencyRuleConfig)
                .isNotSameAs(expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig)
                .isEqualTo(expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig);

        assertThat(actualScanLambdaFunctionWithDisallowedArchitectureRuleConfig)
                .isNotSameAs(expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig)
                .isEqualTo(expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig);

        assertThat(actualScanGlueSessionActiveWithLongIdleTimeoutRuleConfig)
                .isNotSameAs(expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig)
                .isEqualTo(expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig);

        assertThat(actualScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig)
                .isNotSameAs(expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig)
                .isEqualTo(expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig);
    }

    @Test
    void testThatRuleSetGetRuleConfigReturnsNullIfRuleConfigIsNull() {
        // Given ruleDefinitions
        // When
        final var actualScanDynamodbTableIdleRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE);
        final var actualScanLambdaFunctionWithUnboundedConcurrencyRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY);
        final var actualScanLambdaFunctionWithDisallowedArchitectureRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE);
        final var actualScanGlueSessionActiveWithLongIdleTimeoutRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
        final var actualScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig =
                testObject.getRuleConfig(RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY);

        // Then
        assertThat(actualScanDynamodbTableIdleRuleConfig).isNull();
        assertThat(actualScanLambdaFunctionWithUnboundedConcurrencyRuleConfig).isNull();
        assertThat(actualScanLambdaFunctionWithDisallowedArchitectureRuleConfig).isNull();
        assertThat(actualScanGlueSessionActiveWithLongIdleTimeoutRuleConfig).isNull();
        assertThat(actualScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig).isNull();
    }
}
