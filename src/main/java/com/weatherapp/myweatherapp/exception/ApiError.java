package com.weatherapp.myweatherapp.exception;
import java.time.LocalDateTime;

public class ApiError {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private String status;

    public ApiError(String message, String details, String status) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
        this.status = status;
    }

    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
}