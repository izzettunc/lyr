package com.lyr.rule.lambda.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig implements RuleConfig {

    public static ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig create() {
        return ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
    }

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of();
    }

    @Override
    public RuleConfig copy() {
        return ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
    }
}
