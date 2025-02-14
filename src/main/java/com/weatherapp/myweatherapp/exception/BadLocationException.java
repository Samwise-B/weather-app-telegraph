package com.weatherapp.myweatherapp.exception;

public class BadLocationException extends RuntimeException {
    public BadLocationException(String message) {
        super(message);
    }

    public BadLocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
