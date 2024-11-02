package ru.practicum.ewm.main.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String stackTrace;

    public ApiError(String status, String reason, String message, String stackTrace) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.stackTrace = stackTrace;
    }
}
