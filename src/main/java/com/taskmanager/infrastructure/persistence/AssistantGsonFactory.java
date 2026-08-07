package com.taskmanager.infrastructure.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskmanager.domain.assistant.TaskSuggestion;

public final class AssistantGsonFactory {

    private AssistantGsonFactory() {}

    public static Gson create() {
        return new GsonBuilder()
                .registerTypeAdapter(TaskSuggestion.class, new TaskSuggestionTypeAdapter())
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }
}