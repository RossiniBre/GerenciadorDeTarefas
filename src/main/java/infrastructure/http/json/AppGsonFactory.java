package infrastructure.http.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.assistant.TaskSuggestion;
import infrastructure.persistence.TaskSuggestionAdapter;
import infrastructure.persistence.LocalDateTimeTypeAdapter;

import java.time.LocalDateTime;

public class AppGsonFactory {
    public static Gson create() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(TaskSuggestion.class, new TaskSuggestionAdapter())
                .create();
    }
}
