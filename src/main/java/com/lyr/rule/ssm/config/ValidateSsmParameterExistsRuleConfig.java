package com.lyr.rule.ssm.config;

import com.lyr.rule.RuleConfig;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ValidateSsmParameterExistsRuleConfig implements RuleConfig {

    private List<String> parameterNames;

    public static ValidateSsmParameterExistsRuleConfig parse(final Object config) {
        if (!(config instanceof List)) {
            throw new IllegalArgumentException("Invalid config type for ValidateSsmParameterExistsRuleConfig");
        }

        return ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames((List<String>) config)
                .build();
    }
}
