package com.lyr.rule.glue;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.services.glue.GlueConnector;
import java.util.List;
import software.amazon.awssdk.services.glue.model.Session;
import software.amazon.awssdk.services.glue.model.SessionStatus;

public class ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution
        implements RuleExecutionStrategy<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    private static final List<SessionStatus> ACTIVE_STATUSES =
            ImmutableList.of(SessionStatus.PROVISIONING, SessionStatus.READY);

    @Override
    public ImmutableList<Finding> execute(final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parameters) {
        return GlueConnector.create().getSessionHistory().stream()
                .filter(session -> ACTIVE_STATUSES.contains(session.status())
                        && session.idleTimeout() > parameters.getMaxIdleTimeoutInMinutes())
                .map(Session::id)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());
    }
}
