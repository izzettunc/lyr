package com.example.rule.ssm.config;

import com.example.rule.RuleConfig;
import com.example.rule.lambda.config.ValidateLambdaFunctionConcurrencyRuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class ValidateSsmParameterValueRuleConfig implements RuleConfig {
    public static final String NAME = "validate.ssm.parameter.value";

    public record SsmParameterValue(String parameterName, String value) {
    }

    private List<SsmParameterValue> parameterValues;

    public static ValidateSsmParameterValueRuleConfig parse(Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException("Invalid config type for ValidateSsmParameterValueRuleConfig");
        }

        var ssmParameterValues = ((Map<String, String>) config)
                .entrySet()
                .stream()
                .map(entry ->
                        new SsmParameterValue(entry.getKey(), entry.getValue()))
                .toList();

        return ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(ssmParameterValues)
                .build();
    }
}
