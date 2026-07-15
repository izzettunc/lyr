package com.lyr.rule.glue;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.services.glue.GlueConnector;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.glue.model.Session;
import software.amazon.awssdk.services.glue.model.SessionStatus;

@Slf4j
public class ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution
        implements RuleExecutionStrategy<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    private static final List<SessionStatus> ACTIVE_STATUSES =
            ImmutableList.of(SessionStatus.PROVISIONING, SessionStatus.READY);

    @Override
    public ImmutableList<Finding> execute(final ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parameters) {
        final var listOfSessions = GlueConnector.create().getSessionHistory();
        final var findings = listOfSessions.stream()
                .filter(session -> ACTIVE_STATUSES.contains(session.status())
                        && session.idleTimeout() > parameters.getMaxIdleTimeoutInMinutes())
                .map(Session::id)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(listOfSessions.size())
                .log("Found {} session(s) with problems out of {} session(s).");

        return findings;
    }
}
