package infrastructure.persistence;

import com.google.gson.*;
import domain.assistant.TaskSuggestion;

import java.lang.reflect.Type;

public class TaskSuggestionAdapter implements JsonSerializer<TaskSuggestion>, JsonDeserializer<TaskSuggestion> {

    @Override
    public JsonElement serialize(TaskSuggestion src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = context.serialize(src, src.getClass()).getAsJsonObject();
        String type = switch (src) {
            case TaskSuggestion.Create c -> "CREATE";
            case TaskSuggestion.Update u -> "UPDATE";
            case TaskSuggestion.Delete d -> "DELETE";
            case TaskSuggestion.Start s -> "START";
            case TaskSuggestion.Complete c -> "COMPLETE";
        };
        json.addProperty("type", type);
        return json;
    }

    @Override
    public TaskSuggestion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        if (!obj.has("type")) {
            throw new JsonParseException("Campo 'type' ausente ao desserializar TaskSuggestion.");
        }
        String type = obj.get("type").getAsString();
        return switch (type) {
            case "CREATE" -> context.deserialize(obj, TaskSuggestion.Create.class);
            case "UPDATE" -> context.deserialize(obj, TaskSuggestion.Update.class);
            case "DELETE" -> context.deserialize(obj, TaskSuggestion.Delete.class);
            case "START" -> context.deserialize(obj, TaskSuggestion.Start.class);
            case "COMPLETE" -> context.deserialize(obj, TaskSuggestion.Complete.class);
            default -> throw new JsonParseException("Tipo de sugestão desconhecido: " + type);
        };
    }
}