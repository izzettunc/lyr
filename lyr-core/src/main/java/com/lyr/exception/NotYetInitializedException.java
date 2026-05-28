package com.lyr.exception;

import java.io.Serial;

public class NotYetInitializedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4088122968776485251L;

    public NotYetInitializedException(final String message) {
        super(message);
    }
}
