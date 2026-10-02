package com.lyr.config.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.lyr.rule.RuleConfig;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableWithoutBackupRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig;
import com.lyr.util.RuleDefinition;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RuleSetTest {

    RuleSet testObject;

    @BeforeEach
    void beforeEach() {
        testObject = new RuleSet();
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig = ScanDynamodbTableIdleRuleConfig.builder().build();
        // When
        testObject.setScanDynamodbTableIdleRuleConfig(null);
        // Then
        assertThat(testObject.scanDynamodbTableIdleRuleConfig).isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableIdleRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(9999)
                .build();
        // When
        testObject.setScanDynamodbTableIdleRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanDynamodbTableIdleRuleConfig).isSameAs(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableWithoutBackupRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanDynamodbTableWithoutBackupRuleConfig.builder().build();
        // When
        testObject.setScanDynamodbTableWithoutBackupRuleConfig(null);
        // Then
        assertThat(testObject.scanDynamodbTableWithoutBackupRuleConfig).isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanDynamodbTableWithoutBackupRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfPitrEnabled(false)
                .passIfBackupPlanEnabled(true)
                .build();
        // When
        testObject.setScanDynamodbTableWithoutBackupRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanDynamodbTableWithoutBackupRuleConfig).isSameAs(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        // When
        testObject.setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(null);
        // Then
        assertThat(testObject.scanLambdaFunctionWithUnboundedConcurrencyRuleConfig)
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
        // When
        testObject.setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanLambdaFunctionWithUnboundedConcurrencyRuleConfig)
                .isSameAs(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithDisallowedArchitectureRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build();
        // When
        testObject.setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(null);
        // Then
        assertThat(testObject.scanLambdaFunctionWithDisallowedArchitectureRuleConfig)
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatScanLambdaFunctionWithDisallowedArchitectureRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig = ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                .disallowedArchitecture("random")
                .build();
        // When
        testObject.setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanLambdaFunctionWithDisallowedArchitectureRuleConfig)
                .isSameAs(expectedRuleConfig);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build();
        // When
        testObject.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(null);
        // Then
        assertThat(testObject.scanGlueSessionActiveWithLongIdleTimeoutRuleConfig)
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(999)
                .build();
        // When
        testObject.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanGlueSessionActiveWithLongIdleTimeoutRuleConfig)
                .isSameAs(expectedRuleConfig);
    }

    @Test
    void testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
        // When
        testObject.setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(null);
        // Then
        assertThat(testObject.scanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig)
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void
            testThatScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
        // When
        testObject.setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig)
                .isSameAs(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithXrayTracingNotEnabledRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig.builder().build();
        // When
        testObject.setScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig(null);
        // Then
        assertThat(testObject.scanLambdaFunctionWithXrayTracingNotEnabledRuleConfig)
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithXrayTracingNotEnabledRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig.builder().build();
        // When
        testObject.setScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanLambdaFunctionWithXrayTracingNotEnabledRuleConfig)
                .isSameAs(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithoutAnyActiveTriggerRuleConfigSetterBuildsDefaultWhenNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder().build();
        // When
        testObject.setScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig(null);
        // Then
        assertThat(testObject.scanLambdaFunctionWithoutAnyActiveTriggerRuleConfig)
                .isEqualTo(expectedRuleConfig);
    }

    @Test
    void testThatScanLambdaFunctionWithoutAnyActiveTriggerRuleConfigSetterUsesGivenValueWhenNonNullAsInputProvided() {
        // Given
        final var expectedRuleConfig =
                ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder().build();
        // When
        testObject.setScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig(expectedRuleConfig);
        // Then
        assertThat(testObject.scanLambdaFunctionWithoutAnyActiveTriggerRuleConfig)
                .isSameAs(expectedRuleConfig);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allRuleDefinitionsWithRandomConfigDataAndSetters")
    <T extends RuleConfig> void testThatRuleSetGetRuleConfigReturnsCopyOfCorrectRuleConfig(
            final RuleDefinition ruleDefinition,
            final T expectedRuleConfig,
            final BiConsumer<RuleSet, T> setRuleConfig) {
        // Given expectRuleConfig, testObject, ruleDefinition
        setRuleConfig.accept(testObject, expectedRuleConfig);

        // When
        final var actualRuleConfig = testObject.getRuleConfig(ruleDefinition);

        // Then
        assertThat(actualRuleConfig).isNotSameAs(expectedRuleConfig).isEqualTo(expectedRuleConfig);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allRuleDefinitions")
    void testThatRuleSetGetRuleConfigReturnsNullIfRuleConfigIsNull(final RuleDefinition ruleDefinition) {
        // Given ruleDefinition
        // When
        final var actualRuleConfig = testObject.getRuleConfig(ruleDefinition);

        // Then
        assertThat(actualRuleConfig).isNull();
    }

    static Stream<Arguments> allRuleDefinitions() {
        return Stream.of(RuleDefinition.values()).map(Arguments::of);
    }

    static Stream<Arguments> allRuleDefinitionsWithRandomConfigDataAndSetters() {
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
        final var expectedScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig =
                ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig.builder().build();
        final var expectedScanLambdaFunctionWithoutTriggerRuleConfig =
                ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder().build();
        final var expectedScanDynamodbTableWithoutBackupRuleConfig = ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfPitrEnabled(false)
                .passIfBackupPlanEnabled(true)
                .build();

        return Stream.of(
                Arguments.of(
                        RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE, expectedScanDynamodbTableIdleRuleConfig, (BiConsumer<
                                        RuleSet, ScanDynamodbTableIdleRuleConfig>)
                                RuleSet::setScanDynamodbTableIdleRuleConfig),
                Arguments.of(
                        RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                        expectedScanLambdaFunctionWithUnboundedConcurrencyRuleConfig,
                        (BiConsumer<RuleSet, ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig>)
                                RuleSet::setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig),
                Arguments.of(
                        RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE,
                        expectedScanLambdaFunctionWithDisallowedArchitectureRuleConfig,
                        (BiConsumer<RuleSet, ScanLambdaFunctionWithDisallowedArchitectureRuleConfig>)
                                RuleSet::setScanLambdaFunctionWithDisallowedArchitectureRuleConfig),
                Arguments.of(
                        RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        expectedScanGlueSessionActiveWithLongIdleTimeoutRuleConfig,
                        (BiConsumer<RuleSet, ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig>)
                                RuleSet::setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig),
                Arguments.of(
                        RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                        expectedScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig,
                        (BiConsumer<RuleSet, ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig>)
                                RuleSet::setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig),
                Arguments.of(
                        RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_XRAY_TRACING_NOT_ENABLED,
                        expectedScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig,
                        (BiConsumer<RuleSet, ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig>)
                                RuleSet::setScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig),
                Arguments.of(
                        RuleDefinition.SCAN_LAMBDA_FUNCTION_WITHOUT_ANY_ACTIVE_TRIGGER,
                        expectedScanLambdaFunctionWithoutTriggerRuleConfig,
                        (BiConsumer<RuleSet, ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig>)
                                RuleSet::setScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig),
                Arguments.of(
                        RuleDefinition.SCAN_DYNAMODB_TABLE_WITHOUT_BACKUP,
                        expectedScanDynamodbTableWithoutBackupRuleConfig,
                        (BiConsumer<RuleSet, ScanDynamodbTableWithoutBackupRuleConfig>)
                                RuleSet::setScanDynamodbTableWithoutBackupRuleConfig));
    }
}
