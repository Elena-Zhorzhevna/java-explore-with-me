package ru.practicum.ewm.main.exception;

import lombok.Getter;

@Getter
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String stackTrace;
    private String timestamp;

    public ApiError(String status, String reason, String message, String stackTrace, String timestamp) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.stackTrace = stackTrace;
        this.timestamp = timestamp;
    }
}
