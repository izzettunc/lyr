package com.lyr.exception.rule.config;

import java.io.Serial;

public class BadRuleConfigException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -878484276999786191L;

    public BadRuleConfigException(final String message) {
        super(message);
    }
}
