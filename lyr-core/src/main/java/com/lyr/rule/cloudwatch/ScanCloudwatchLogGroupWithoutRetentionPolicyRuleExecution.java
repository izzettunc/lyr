package com.lyr.rule.cloudwatch;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

@Slf4j
public class ScanCloudwatchLogGroupWithoutRetentionPolicyRuleExecution
        implements RuleExecutionStrategy<ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig parameters) {
        final var listOfLogGroupsDetails = CloudWatchConnector.create().listLogGroupDetails();
        final var findings = listOfLogGroupsDetails.stream()
                .filter(logGroup -> null == logGroup.retentionInDays())
                .map(LogGroup::logGroupName)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(listOfLogGroupsDetails.size())
                .log("Found {} log group(s) with problems out of {} log group(s).");

        return findings;
    }
}
