package com.lyr.config.parser;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.google.common.annotations.VisibleForTesting;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.util.RuleDefinition;
import java.util.function.Supplier;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RuleSet {

    @VisibleForTesting
    ScanDynamodbTableIdleRuleConfig scanDynamodbTableIdleRuleConfig;

    @VisibleForTesting
    ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig scanGlueSessionActiveWithLongIdleTimeoutRuleConfig;

    @VisibleForTesting
    ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig scanLambdaFunctionWithUnboundedConcurrencyRuleConfig;

    @VisibleForTesting
    ScanLambdaFunctionWithDisallowedArchitectureRuleConfig scanLambdaFunctionWithDisallowedArchitectureRuleConfig;

    @VisibleForTesting
    ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig scanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;

    @JsonSetter(value = "scan.dynamodb.table.idle", nulls = Nulls.SET)
    public void setScanDynamodbTableIdleRuleConfig(final ScanDynamodbTableIdleRuleConfig ruleConfig) {
        this.scanDynamodbTableIdleRuleConfig = defaultIfNull(
                ruleConfig, () -> ScanDynamodbTableIdleRuleConfig.builder().build());
    }

    @JsonSetter(value = "scan.glue.session.activeWithLongIdleTimeout", nulls = Nulls.SET)
    public void setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
            final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig ruleConfig) {
        this.scanGlueSessionActiveWithLongIdleTimeoutRuleConfig = defaultIfNull(
                ruleConfig,
                () -> ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .build());
    }

    @JsonSetter(value = "scan.lambda.function.withUnboundedConcurrency", nulls = Nulls.SET)
    public void setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(
            final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ruleConfig) {
        this.scanLambdaFunctionWithUnboundedConcurrencyRuleConfig = defaultIfNull(
                ruleConfig,
                () -> ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder()
                        .build());
    }

    @JsonSetter(value = "scan.lambda.function.withDisallowedArchitecture", nulls = Nulls.SET)
    public void setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(
            final ScanLambdaFunctionWithDisallowedArchitectureRuleConfig ruleConfig) {
        this.scanLambdaFunctionWithDisallowedArchitectureRuleConfig = defaultIfNull(
                ruleConfig,
                () -> ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder()
                        .build());
    }

    @JsonSetter(value = "scan.cloudwatch.logGroup.withoutRetentionPolicy", nulls = Nulls.SET)
    public void setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(
            final ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig ruleConfig) {
        this.scanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig = defaultIfNull(
                ruleConfig,
                () -> ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder()
                        .build());
    }

    public RuleConfig getRuleConfig(final RuleDefinition ruleDefinition) {
        final var ruleConfig =
                switch (ruleDefinition) {
                    case SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY ->
                        scanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;

                    case SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT ->
                        scanGlueSessionActiveWithLongIdleTimeoutRuleConfig;

                    case SCAN_DYNAMODB_TABLE_IDLE -> scanDynamodbTableIdleRuleConfig;

                    case SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE ->
                        scanLambdaFunctionWithDisallowedArchitectureRuleConfig;

                    case SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY ->
                        scanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
                };

        return copyOrNull(ruleConfig);
    }

    private <T extends RuleConfig> T defaultIfNull(final T config, final Supplier<T> defaultSupplier) {
        return config == null ? defaultSupplier.get() : config;
    }

    private RuleConfig copyOrNull(final RuleConfig ruleConfig) {
        return ruleConfig == null ? null : ruleConfig.copy();
    }
}
