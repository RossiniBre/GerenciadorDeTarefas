package com.taskmanager.infrastructure.assistant;

public class AssistantRequestFailedException extends RuntimeException {
    public AssistantRequestFailedException(String message) {
        super(message);
    }

    public AssistantRequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}