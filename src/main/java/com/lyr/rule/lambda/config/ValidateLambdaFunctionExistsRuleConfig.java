package com.lyr.rule.lambda.config;

import static com.lyr.rule.Constants.VALIDATE_LAMBDA_FUNCTION_EXISTS;

import com.lyr.exception.rule.config.InvalidRuleConfigTypeException;
import com.lyr.rule.RuleConfig;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ValidateLambdaFunctionExistsRuleConfig implements RuleConfig {

    private List<String> functionNames;

    public static ValidateLambdaFunctionExistsRuleConfig parse(final Object config) {
        if (!(config instanceof List)) {
            throw new InvalidRuleConfigTypeException(VALIDATE_LAMBDA_FUNCTION_EXISTS, "list");
        }

        return ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames((List<String>) config)
                .build();
    }
}
