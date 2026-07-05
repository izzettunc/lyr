package com.lyr.rule.lambda.config;

import com.lyr.rule.RuleConfig;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig implements RuleConfig {

    @Override
    public Map<String, String> getConfigAsStringMap() {
        return Map.of();
    }

    @Override
    public RuleConfig copy() {
        return ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
    }
}
