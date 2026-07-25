package com.lyr.exception.rule.config;

import static com.lyr.util.StringUtil.collectionToString;

import java.io.Serial;
import java.util.Collection;

public class BadRuleConfigException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -878484276999786191L;

    private static final String FOR = " for ";

    public BadRuleConfigException(final String message) {
        super(message);
    }

    public static BadRuleConfigException forNull(final String ruleName) {
        final var message = ruleName + " rule config must not be null";
        return new BadRuleConfigException(message);
    }

    public static BadRuleConfigException forLessThanLimit(
            final String configName, final String ruleName, final int limit) {
        final var message = configName + FOR + ruleName + " rule config must be equal or greater than " + limit;
        return new BadRuleConfigException(message);
    }

    public static BadRuleConfigException forUnsupportedValue(
            final String configName, final String ruleName, final Collection<?> allowedValues) {
        final var allowedValuesAsString = collectionToString(allowedValues);
        final var message = configName + FOR + ruleName + " rule config must be one of " + allowedValuesAsString;
        return new BadRuleConfigException(message);
    }

    public static BadRuleConfigException forUnsupportedValues(
            final String configName, final String ruleName, final Collection<?> allowedValues) {
        final var allowedValuesAsString = collectionToString(allowedValues);
        final var message =
                configName + FOR + ruleName + " rule config must consist of elements of " + allowedValuesAsString;
        return new BadRuleConfigException(message);
    }
}
