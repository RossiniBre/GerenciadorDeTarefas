package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskBuilderTest {

    @Test
    void shouldBuildTaskWithDefaultsWhenPriorityAndCategoryNotSet() {
        Task task = new TaskBuilder()
                .title("Estudar Java")
                .description("2h por dia")
                .build();

        assertEquals("Estudar Java", task.getTitle());
        assertEquals("2h por dia", task.getDescription());
        assertEquals(TaskPriority.LOW, task.getPriority());
        assertEquals(TaskCategory.UNCATEGORIZED, task.getCategory());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void shouldBuildTaskWithCustomPriorityAndCategory() {
        Task task = new TaskBuilder()
                .title("Reunião de projeto")
                .description("Alinhar escopo com o time")
                .priority(TaskPriority.HIGH)
                .category(TaskCategory.WORK)
                .build();

        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(TaskCategory.WORK, task.getCategory());
    }

    @Test
    void shouldBuildTaskWithOnlyPriorityCustomized() {
        Task task = new TaskBuilder()
                .title("Ler artigo")
                .priority(TaskPriority.MEDIUM)
                .build();

        assertEquals(TaskPriority.MEDIUM, task.getPriority());
        assertEquals(TaskCategory.UNCATEGORIZED, task.getCategory());
    }

    @Test
    void shouldThrowWhenTitleIsMissing() {
        TaskBuilder builder = new TaskBuilder()
                .description("Sem título definido");

        assertThrows(IllegalArgumentException.class, builder::build);
    }
}