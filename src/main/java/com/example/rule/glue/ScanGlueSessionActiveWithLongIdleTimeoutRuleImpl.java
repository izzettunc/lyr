package com.example.rule.glue;

import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.outcome.Outcome;
import com.example.rule.outcome.ScanOutcome;
import com.example.rule.RuleStrategy;
import com.example.services.glue.GlueConnector;
import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.glue.model.ListSessionsResponse;
import software.amazon.awssdk.services.glue.model.Session;
import software.amazon.awssdk.services.glue.model.SessionStatus;

import java.util.List;

public class ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl implements RuleStrategy<ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig> {

    @Override
    public ImmutableList<Outcome> execute(ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig parameters) {
        return GlueConnector.getInstance()
                .getSessionHistory()
                .stream()
                .map(ListSessionsResponse::sessions)
                .flatMap(List::stream)
                .filter(session -> session.status() == SessionStatus.READY &&
                        session.idleTimeout() > parameters.getMaxIdleTimeoutInMinutes())
                .map(Session::id)
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());
    }
}
