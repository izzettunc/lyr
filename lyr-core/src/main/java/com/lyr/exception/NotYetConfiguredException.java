package com.lyr.exception;

import java.io.Serial;

public class NotYetConfiguredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6928387742916425749L;

    public NotYetConfiguredException(final String message) {
        super(message);
    }
}
