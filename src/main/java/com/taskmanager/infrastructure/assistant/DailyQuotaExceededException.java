package com.taskmanager.infrastructure.assistant;

import java.time.LocalDateTime;

public class DailyQuotaExceededException extends RuntimeException {

    private final LocalDateTime resetsAt;

    public DailyQuotaExceededException(LocalDateTime resetsAt) {
        super("Cota diária do assistente esgotada. Libera em: " + resetsAt);
        this.resetsAt = resetsAt;
    }

    public LocalDateTime getResetsAt() {
        return resetsAt;
    }
}