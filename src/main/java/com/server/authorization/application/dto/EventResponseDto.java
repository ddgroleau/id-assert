package com.server.authorization.application.dto;

public class EventResponseDto {
    private boolean isSuccess;
    private String message;

    private EventResponseDto(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public static EventResponseDto createResponse(boolean isSuccess, String message) {
        return new EventResponseDto(isSuccess,message);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }
}
