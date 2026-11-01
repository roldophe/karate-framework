package com.example.karateframework.exception;

import java.io.Serial;

public class ExternalApiException extends RuntimeException {

    @Serial private static final long serialVersionUID = 1L;

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
