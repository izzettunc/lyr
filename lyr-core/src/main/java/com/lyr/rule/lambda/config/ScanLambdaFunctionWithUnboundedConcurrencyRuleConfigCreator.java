package com.lyr.rule.lambda.config;

import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;

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
            throw BadRuleConfigException.forNull(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY.getRuleName());
        }
    }
}
