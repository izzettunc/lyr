package com.lyr.rule.dynamodb.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@Builder
@Value
@JsonDeserialize(
        builder =
                ScanDynamodbTableWithoutDeletionProtectionRuleConfig
                        .ScanDynamodbTableWithoutDeletionProtectionRuleConfigBuilder.class)
public class ScanDynamodbTableWithoutDeletionProtectionRuleConfig implements RuleConfig {

    @Override
    public Map<String, Object> getConfigAsMap() {
        return Map.of();
    }

    @Override
    public ScanDynamodbTableWithoutDeletionProtectionRuleConfig copy() {
        return ScanDynamodbTableWithoutDeletionProtectionRuleConfig.builder().build();
    }

    @Override
    public void validate() {}

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanDynamodbTableWithoutDeletionProtectionRuleConfigBuilder {}
}
