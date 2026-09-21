package com.lyr.rule.dynamodb.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@Builder
@Value
@JsonDeserialize(
        builder = ScanDynamodbTableWithoutBackupRuleConfig.ScanDynamodbTableWithoutBackupRuleConfigBuilder.class)
public class ScanDynamodbTableWithoutBackupRuleConfig implements RuleConfig {
    public static final String PASS_IF_PITR_ENABLED_CONFIG_KEY = "passIfPITREnabled";
    public static final String PASS_IF_BACKUP_PLAN_ENABLED_CONFIG_KEY = "passIfBackupPlanEnabled";

    public static final Boolean DEFAULT_PASS_IF_PITR_ENABLED = Boolean.TRUE;
    public static final Boolean DEFAULT_PASS_IF_BACKUP_PLAN_ENABLED = Boolean.TRUE;

    @NonNull
    @Builder.Default
    Boolean passIfPitrEnabled = DEFAULT_PASS_IF_PITR_ENABLED;

    @NonNull
    @Builder.Default
    Boolean passIfBackupPlanEnabled = DEFAULT_PASS_IF_BACKUP_PLAN_ENABLED;

    @Override
    public Map<String, Object> getConfigAsMap() {
        return Map.of(
                PASS_IF_PITR_ENABLED_CONFIG_KEY, passIfPitrEnabled,
                PASS_IF_BACKUP_PLAN_ENABLED_CONFIG_KEY, passIfBackupPlanEnabled);
    }

    @Override
    public ScanDynamodbTableWithoutBackupRuleConfig copy() {
        return ScanDynamodbTableWithoutBackupRuleConfig.builder()
                .passIfPitrEnabled(passIfPitrEnabled)
                .passIfBackupPlanEnabled(passIfBackupPlanEnabled)
                .build();
    }

    @Override
    public void validate() {}

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanDynamodbTableWithoutBackupRuleConfigBuilder {}
}
