package com.lyr.rule.cloudwatch.config;

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
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig
                        .ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigBuilder.class)
public class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig implements RuleConfig {

    @Override
    public Map<String, Object> getConfigAsMap() {
        return Map.of();
    }

    @Override
    public ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig copy() {
        return ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
    }

    @Override
    public void validate() {}

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigBuilder {}
}
