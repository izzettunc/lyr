package com.lyr.rule.ssm.config;

import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;

import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
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
            throw new InvalidRuleConfigTypeException(VALIDATE_SSM_PARAMETER_EXISTS, "list");
        }

        return ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames((List<String>) config)
                .build();
    }
}
