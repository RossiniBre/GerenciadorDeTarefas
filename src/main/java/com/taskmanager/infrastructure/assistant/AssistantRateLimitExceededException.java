package com.taskmanager.infrastructure.assistant;

import java.time.LocalDateTime;

public class AssistantRateLimitExceededException extends AssistantRequestFailedException {

    private final LocalDateTime resetsAt;

    public AssistantRateLimitExceededException(String message, LocalDateTime resetsAt) {
        super(message);
        this.resetsAt = resetsAt;
    }

    public LocalDateTime getResetsAt() {
        return resetsAt;
    }
}