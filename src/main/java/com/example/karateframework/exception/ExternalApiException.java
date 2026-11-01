package com.example.karateframework.exception;

public class ExternalApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
