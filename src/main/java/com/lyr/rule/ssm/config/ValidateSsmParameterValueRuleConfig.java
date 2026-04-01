package com.lyr.rule.ssm.config;

import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;

import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.rule.RuleConfig;
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

    public static ValidateSsmParameterValueRuleConfig parse(final Object config) {
        if (!(config instanceof Map)) {
            throw new InvalidRuleConfigTypeException(VALIDATE_SSM_PARAMETER_VALUE, "map");
        }

        final var ssmParameterValues = ((Map<String, String>) config)
                .entrySet().stream()
                        .map(entry -> new SsmParameterValue(entry.getKey(), entry.getValue()))
                        .toList();

        return ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(ssmParameterValues)
                .build();
    }
}
