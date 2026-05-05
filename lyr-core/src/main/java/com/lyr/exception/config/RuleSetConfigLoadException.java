package com.lyr.exception.config;

import java.io.Serial;

public class RuleSetConfigLoadException extends RuntimeException {

    public static final String FAILED_GENERATE_RULE_SET_CONFIG = "Failed to generate rule set config. Exception: %s";

    @Serial
    private static final long serialVersionUID = 6982438612491865025L;

    public RuleSetConfigLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
