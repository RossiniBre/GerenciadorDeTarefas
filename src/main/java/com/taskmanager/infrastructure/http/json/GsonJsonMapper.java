package com.taskmanager.infrastructure.http.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskmanager.infrastructure.persistence.LocalDateTimeTypeAdapter;

import java.time.LocalDateTime;

public class GsonJsonMapper implements JsonMapper {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    @Override
    public String toJson(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}