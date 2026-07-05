package com.lyr.rule.cloudwatch.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig implements RuleConfig {

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of();
    }

    @Override
    public RuleConfig copy() {
        return ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
    }
}
