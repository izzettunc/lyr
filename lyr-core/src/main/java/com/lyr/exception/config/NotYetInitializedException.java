package com.lyr.exception.config;

import java.io.Serial;

public class NotYetInitializedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6376451924690333285L;

    public NotYetInitializedException(final String message) {
        super(message);
    }
}
