package com.example.rule.ssm.config;

import com.example.rule.RuleConfig;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ValidateSsmParameterValueRuleConfig implements RuleConfig {

    public record SsmParameterValue(String parameterName, String value) {}

    private List<SsmParameterValue> parameterValues;

    public static ValidateSsmParameterValueRuleConfig parse(Object config) {
        if (!(config instanceof Map)) {
            throw new IllegalArgumentException("Invalid config type for ValidateSsmParameterValueRuleConfig");
        }

        var ssmParameterValues = ((Map<String, String>) config)
                .entrySet().stream()
                        .map(entry -> new SsmParameterValue(entry.getKey(), entry.getValue()))
                        .toList();

        return ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(ssmParameterValues)
                .build();
    }
}
