package com.lyr.exception.rule.config;

import java.io.Serial;

public class InvalidRuleConfigTypeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 404604451522980354L;

    public InvalidRuleConfigTypeException(final String rule, final String expectedType) {
        super("Provided config for " + rule + " rule is not a " + expectedType);
    }
}
