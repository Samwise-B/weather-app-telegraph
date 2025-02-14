package com.weatherapp.myweatherapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;


@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ApiError> handleBadRequestException(HttpClientErrorException.BadRequest ex) {
        logger.error("Bad request error: ", ex);
        ApiError error = new ApiError(
            "Invalid request",
            "The request to the weather service was invalid",
            HttpStatus.BAD_REQUEST.toString()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<ApiError> handleTooManyRequestsException(HttpClientErrorException.TooManyRequests ex) {
        logger.error("Rate limit exceeded: ", ex);
        System.out.println("TEST TEST TEST");
        ApiError error = new ApiError(
            "Rate limit exceeded",
            "The weather service rate limit has been exceeded. Please try again later.",
            HttpStatus.TOO_MANY_REQUESTS.toString()
        );
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(HttpClientErrorException.Unauthorized ex) {
        logger.error("API key error: ", ex);
        ApiError error = new ApiError(
            "Authentication failed",
            "Failed to authenticate with the weather service",
            HttpStatus.UNAUTHORIZED.toString()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<ApiError> handleWeatherApiException(WeatherApiException ex) {
        logger.error("Weather API error: ", ex);
        ApiError error = new ApiError(
            "Weather service error",
            ex.getMessage(),
            HttpStatus.BAD_GATEWAY.toString()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex) {
        logger.error("Unexpected error: ", ex);
        ApiError error = new ApiError(
            "Internal server error",
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.toString()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

