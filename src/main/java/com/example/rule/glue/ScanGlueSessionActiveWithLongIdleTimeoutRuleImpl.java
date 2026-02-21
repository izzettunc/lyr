package com.example.rule.glue;

import com.example.rule.RuleStrategy;
import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.example.services.glue.GlueConnector;
import com.google.common.collect.ImmutableList;
import java.util.List;
import software.amazon.awssdk.services.glue.model.ListSessionsResponse;
import software.amazon.awssdk.services.glue.model.Session;
import software.amazon.awssdk.services.glue.model.SessionStatus;

public class ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl
        implements RuleStrategy<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    private static final List<SessionStatus> invalidStatuses =
            ImmutableList.of(SessionStatus.PROVISIONING, SessionStatus.READY);

    @Override
    public ImmutableList<Outcome> execute(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parameters) {
        return GlueConnector.create().getSessionHistory().stream()
                .map(ListSessionsResponse::sessions)
                .flatMap(List::stream)
                .filter(session -> invalidStatuses.contains(session.status())
                        && session.idleTimeout() > parameters.getMaxIdleTimeoutInMinutes())
                .map(Session::id)
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());
    }
}
