package infrastructure;

import com.google.gson.Gson;
import domain.assistant.TaskSuggestion;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import infrastructure.persistence.AssistantGsonFactory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskSuggestionTypeAdapterTest {

    private final Gson gson = AssistantGsonFactory.create();

    @Test
    void shouldSerializeAndDeserializeCreateSuggestion() {
        TaskSuggestion original = new TaskSuggestion.Create(
                UUID.randomUUID(),
                "Comprar pão",
                "Padaria",
                TaskPriority.HIGH,
                TaskCategory.PERSONAL
        );

        String json = gson.toJson(original, TaskSuggestion.class);

        assertTrue(json.contains("\"type\":\"CREATE\""));

        TaskSuggestion restored = gson.fromJson(json, TaskSuggestion.class);

        assertInstanceOf(TaskSuggestion.Create.class, restored);
        assertEquals(original, restored);
    }

    @Test
    void shouldSerializeAndDeserializeUpdateSuggestion() {

        TaskSuggestion original = new TaskSuggestion.Update(
                UUID.randomUUID(),
                "task-1",
                "Novo título",
                "Nova descrição",
                TaskPriority.MEDIUM,
                TaskCategory.WORK
        );

        String json = gson.toJson(original, TaskSuggestion.class);

        assertTrue(json.contains("\"type\":\"UPDATE\""));

        TaskSuggestion restored = gson.fromJson(json, TaskSuggestion.class);

        assertInstanceOf(TaskSuggestion.Update.class, restored);
        assertEquals(original, restored);
    }

    @Test
    void shouldSerializeAndDeserializeDeleteSuggestion() {

        TaskSuggestion original =
                new TaskSuggestion.Delete(
                        UUID.randomUUID(),
                        "task-1"
                );

        String json = gson.toJson(original, TaskSuggestion.class);

        assertTrue(json.contains("\"type\":\"DELETE\""));

        TaskSuggestion restored = gson.fromJson(json, TaskSuggestion.class);

        assertInstanceOf(TaskSuggestion.Delete.class, restored);
        assertEquals(original, restored);
    }

    @Test
    void shouldSerializeAndDeserializeStartSuggestion() {

        TaskSuggestion original =
                new TaskSuggestion.Start(
                        UUID.randomUUID(),
                        "task-1"
                );

        String json = gson.toJson(original, TaskSuggestion.class);

        assertTrue(json.contains("\"type\":\"START\""));

        TaskSuggestion restored = gson.fromJson(json, TaskSuggestion.class);

        assertInstanceOf(TaskSuggestion.Start.class, restored);
        assertEquals(original, restored);
    }

    @Test
    void shouldSerializeAndDeserializeCompleteSuggestion() {

        TaskSuggestion original =
                new TaskSuggestion.Complete(
                        UUID.randomUUID(),
                        "task-1"
                );

        String json = gson.toJson(original, TaskSuggestion.class);

        assertTrue(json.contains("\"type\":\"COMPLETE\""));

        TaskSuggestion restored = gson.fromJson(json, TaskSuggestion.class);

        assertInstanceOf(TaskSuggestion.Complete.class, restored);
        assertEquals(original, restored);
    }
}