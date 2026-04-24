package com.lyr.exception.config;

import java.io.Serial;

public class AppSettingsNotYetInitializedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6376451924690333285L;

    public AppSettingsNotYetInitializedException(final String message) {
        super(message);
    }
}
