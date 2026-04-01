package com.lyr.exception.rule;

import java.io.Serial;

public class UnknownRuleException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8920467111346251465L;

    public UnknownRuleException(final String message) {
        super(message);
    }
}
