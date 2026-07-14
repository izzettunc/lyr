package com.lyr.exception.config;

import java.io.Serial;

public class RuleSetParseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8852915982163582073L;

    public RuleSetParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
