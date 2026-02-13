package com.example.rule.lambda.config;

import com.example.rule.RuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig implements RuleConfig {
    public static final String NAME = "scan.lambda.function.withUnboundedConcurrency";

    public static ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig parse(Object config) {
        // No config available for this rule
        return null;
    }
}
