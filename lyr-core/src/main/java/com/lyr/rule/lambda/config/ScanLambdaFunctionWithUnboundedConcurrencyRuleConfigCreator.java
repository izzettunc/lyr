package com.lyr.rule.lambda.config;

import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfigCreator;
import java.util.Optional;

public class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigCreator
        extends RuleConfigCreator<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> {

    @Override
    protected Optional<ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig> parse(final Object input) {
        return Optional.empty();
    }

    @Override
    protected ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig createDefaultConfig() {
        return ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();
    }

    @Override
    protected void validate(final ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig ruleConfig) {
        if (ruleConfig == null) {
            throw new BadRuleConfigException("Rule config must not be null");
        }
    }
}
