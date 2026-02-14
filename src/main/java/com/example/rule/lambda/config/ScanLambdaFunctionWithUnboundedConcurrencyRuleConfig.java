package com.example.rule.lambda.config;

import com.example.rule.RuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig implements RuleConfig {

    public static ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig parse(Object ignore) {
        // No config available for this rule
        return null;
    }
}
