package com.lyr.rule.lambda;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.RuleExecutionStrategy;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithHighErrorRateRuleConfig;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import com.lyr.services.cloudwatch.CloudWatchUtil;
import com.lyr.services.lambda.LambdaConnector;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScanLambdaFunctionWithHighErrorRateRuleExecution
        implements RuleExecutionStrategy<ScanLambdaFunctionWithHighErrorRateRuleConfig> {

    @Override
    public ImmutableList<Finding> execute(final ScanLambdaFunctionWithHighErrorRateRuleConfig parameters) {
        final var cloudWatchConnector = CloudWatchConnector.create();
        final var lambdaConnector = LambdaConnector.create();

        final var timeRange = calculateTimeRange(parameters);
        final var threshold = parameters.getErrorRateThreshold();

        final var functionNames = lambdaConnector.listLambdaFunctionNames();

        final var findings = functionNames.stream()
                .filter(functionName ->
                        isErrorRateHigherThanThreshold(functionName, cloudWatchConnector, timeRange, threshold))
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        log.atInfo()
                .addArgument(findings.size())
                .addArgument(functionNames.size())
                .log("Found {} function(s) with problems out of {} function(s).");

        return findings;
    }

    private static CloudWatchConnector.TimeRange calculateTimeRange(
            final ScanLambdaFunctionWithHighErrorRateRuleConfig config) {

        final var now = Instant.now();
        final var then = now.minus(Duration.ofDays(config.getTimeSpanInDays()));
        final var appropriateTimeWindow = CloudWatchUtil.getAppropriateTimeWindowForPeriod(config.getTimeSpanInDays());
        final var periodInSeconds = (int) Duration.ofDays(appropriateTimeWindow).getSeconds();

        return CloudWatchConnector.TimeRange.builder()
                .startTime(then)
                .endTime(now)
                .periodInSeconds(periodInSeconds)
                .build();
    }

    private static boolean isErrorRateHigherThanThreshold(
            final String functionName,
            final CloudWatchConnector cloudWatchConnector,
            final CloudWatchConnector.TimeRange timeRange,
            final Double threshold) {

        final var invocations = cloudWatchConnector.getTotalInvocationsOfALambdaFunction(functionName, timeRange);

        if (invocations == 0) {
            return false;
        }

        final var errors = cloudWatchConnector.getTotalErrorsOfALambdaFunction(functionName, timeRange);

        final var errorRate = 100d * errors / invocations;
        return errorRate >= threshold;
    }
}
