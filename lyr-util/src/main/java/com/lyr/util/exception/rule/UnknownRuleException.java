package com.lyr.util.exception.rule;

import java.io.Serial;

public class UnknownRuleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -502810011121290811L;

    public UnknownRuleException(final String message) {
        super(message);
    }
}
