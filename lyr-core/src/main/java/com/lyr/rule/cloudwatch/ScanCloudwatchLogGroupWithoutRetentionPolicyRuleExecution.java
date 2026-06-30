package com.lyr.rule.cloudwatch;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

public class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution
        implements RuleExecutionStrategy<ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig parameters) {
        return CloudWatchConnector.create().listLogGroupDetails().stream()
                .filter(logGroup -> null == logGroup.retentionInDays())
                .map(LogGroup::logGroupName)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());
    }
}
