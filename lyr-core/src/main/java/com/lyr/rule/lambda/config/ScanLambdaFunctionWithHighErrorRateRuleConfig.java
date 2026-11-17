package com.lyr.rule.lambda.config;

import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_HIGH_ERROR_RATE;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@Builder
@Value
@JsonDeserialize(
        builder =
                ScanLambdaFunctionWithHighErrorRateRuleConfig.ScanLambdaFunctionWithHighErrorRateRuleConfigBuilder
                        .class)
public class ScanLambdaFunctionWithHighErrorRateRuleConfig implements RuleConfig {
    public static final String TIME_SPAN_IN_DAYS_CONFIG_KEY = "timeSpanInDays";
    public static final String ERROR_RATE_THRESHOLD_CONFIG_KEY = "errorRateThreshold";
    public static final Integer DEFAULT_TIME_SPAN_IN_DAYS = 30;
    public static final Double DEFAULT_ERROR_RATE_THRESHOLD = 50d;

    private static final int MAX_TIME_SPAN_IN_DAYS = 455;
    private static final double MAX_ERROR_RATE_THRESHOLD = 100d;

    @Builder.Default
    @NonNull
    Integer timeSpanInDays = DEFAULT_TIME_SPAN_IN_DAYS;

    @Builder.Default
    @NonNull
    Double errorRateThreshold = DEFAULT_ERROR_RATE_THRESHOLD;

    @Override
    public Map<String, Object> getConfigAsMap() {
        return Map.of(
                TIME_SPAN_IN_DAYS_CONFIG_KEY, timeSpanInDays, ERROR_RATE_THRESHOLD_CONFIG_KEY, errorRateThreshold);
    }

    @Override
    public ScanLambdaFunctionWithHighErrorRateRuleConfig copy() {
        return ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .timeSpanInDays(timeSpanInDays)
                .errorRateThreshold(errorRateThreshold)
                .build();
    }

    @Override
    public void validate() {
        if (timeSpanInDays < 1 || timeSpanInDays > MAX_TIME_SPAN_IN_DAYS) {
            throw BadRuleConfigException.forOutOfBounds(
                    TIME_SPAN_IN_DAYS_CONFIG_KEY,
                    SCAN_LAMBDA_FUNCTION_WITH_HIGH_ERROR_RATE.getRuleName(),
                    1,
                    MAX_TIME_SPAN_IN_DAYS);
        }
        if (errorRateThreshold < 0 || errorRateThreshold > MAX_ERROR_RATE_THRESHOLD) {
            throw BadRuleConfigException.forOutOfBounds(
                    ERROR_RATE_THRESHOLD_CONFIG_KEY,
                    SCAN_LAMBDA_FUNCTION_WITH_HIGH_ERROR_RATE.getRuleName(),
                    0,
                    MAX_ERROR_RATE_THRESHOLD);
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ScanLambdaFunctionWithHighErrorRateRuleConfigBuilder {}
}
