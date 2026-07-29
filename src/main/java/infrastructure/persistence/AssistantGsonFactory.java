package infrastructure.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.assistant.TaskSuggestion;

public final class AssistantGsonFactory {

    private AssistantGsonFactory() {}

    public static Gson create() {
        return new GsonBuilder().registerTypeAdapter(TaskSuggestion.class, new TaskSuggestionTypeAdapter()).create();
    }
}