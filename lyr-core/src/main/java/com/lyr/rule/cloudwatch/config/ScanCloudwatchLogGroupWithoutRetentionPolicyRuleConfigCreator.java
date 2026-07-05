package com.lyr.rule.cloudwatch.config;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfigCreator;
import java.util.Optional;

public class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfigCreator
        extends RuleConfigCreator<ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig> {

    @Override
    protected Optional<ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig> parse(final Object input) {
        return Optional.empty();
    }

    @Override
    protected ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig createDefaultConfig() {
        return ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build();
    }

    @Override
    protected void validate(final ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig ruleConfig) {
        if (ruleConfig == null) {
            throw new BadRuleConfigException("Rule config must not be null");
        }
    }
}
