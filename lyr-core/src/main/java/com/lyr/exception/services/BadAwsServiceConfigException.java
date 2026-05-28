package com.lyr.exception.services;

import java.io.Serial;

public class BadAwsServiceConfigException extends RuntimeException {
    public static final String BAD_AWS_SERVICE_CONFIG_EXCEPTION_MESSAGE =
            "Failed to create a service using configured credentials. Exception: %s";

    @Serial
    private static final long serialVersionUID = 6376451924690333285L;

    public BadAwsServiceConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BadAwsServiceConfigException(final String message) {
        super(message);
    }
}
