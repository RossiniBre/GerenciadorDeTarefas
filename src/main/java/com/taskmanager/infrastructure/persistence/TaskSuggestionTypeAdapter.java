package com.taskmanager.infrastructure.persistence;

import com.google.gson.*;
import com.taskmanager.domain.assistant.TaskSuggestion;

import java.lang.reflect.Type;

public class TaskSuggestionTypeAdapter
        implements JsonSerializer<TaskSuggestion>, JsonDeserializer<TaskSuggestion> {

    @Override
    public JsonElement serialize(
            TaskSuggestion src,
            Type typeOfSrc,
            JsonSerializationContext context
    ) {
        JsonObject json = context.serialize(src).getAsJsonObject();

        switch (src) {
            case TaskSuggestion.Create ignored ->
                    json.addProperty("type", "CREATE");

            case TaskSuggestion.Update ignored ->
                    json.addProperty("type", "UPDATE");

            case TaskSuggestion.Delete ignored ->
                    json.addProperty("type", "DELETE");

            case TaskSuggestion.Start ignored ->
                    json.addProperty("type", "START");

            case TaskSuggestion.Complete ignored ->
                    json.addProperty("type", "COMPLETE");
        }

        return json;
    }

    @Override
    public TaskSuggestion deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
    ) throws JsonParseException {

        JsonObject object = json.getAsJsonObject();

        String type = object.get("type").getAsString();

        return switch (type) {
            case "CREATE" ->
                    context.deserialize(object, TaskSuggestion.Create.class);

            case "UPDATE" ->
                    context.deserialize(object, TaskSuggestion.Update.class);

            case "DELETE" ->
                    context.deserialize(object, TaskSuggestion.Delete.class);

            case "START" ->
                    context.deserialize(object, TaskSuggestion.Start.class);

            case "COMPLETE" ->
                    context.deserialize(object, TaskSuggestion.Complete.class);

            default ->
                    throw new JsonParseException(
                            "Unknown TaskSuggestion type: " + type
                    );
        };
    }
}