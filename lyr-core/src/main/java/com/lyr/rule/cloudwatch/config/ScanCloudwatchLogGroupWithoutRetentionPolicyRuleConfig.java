package com.lyr.rule.cloudwatch.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig implements RuleConfig {

    public static ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig parse(final Object ignored) {
        // No config available for this rule
        return ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
    }

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of();
    }

    @Override
    public RuleConfig copy() {
        return ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
    }
}
