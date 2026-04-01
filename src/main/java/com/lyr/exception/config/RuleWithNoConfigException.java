package com.lyr.exception.config;

import java.io.Serial;

public class RuleWithNoConfigException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2137195671120496142L;

    public RuleWithNoConfigException(final String message) {
        super(message);
    }
}
