package com.lyr.exception.rule.config;

import java.io.Serial;

public class MissingMandatoryRuleConfigAttributeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7714906195236589224L;

    public MissingMandatoryRuleConfigAttributeException(final String message) {
        super(message);
    }
}
