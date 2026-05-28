package com.lyr.exception;

import java.io.Serial;

public class NotYetConfiguredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6376451924690333285L;

    public NotYetConfiguredException(final String message) {
        super(message);
    }
}
