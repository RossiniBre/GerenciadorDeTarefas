package application;

import domain.Task;
import domain.TaskStatus;
import infrastructure.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateTaskUseCaseTest {

    @Test
    void shouldCreateTaskWithPendingStatus() {
        // Arrange: o que você precisa instanciar aqui?
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        CreateTaskUseCase useCase = new CreateTaskUseCase(repo);
        // Act: chama o execute
        Task task = useCase.execute("Titulo original", "Descricao original");

        // Assert: confere título, descrição e status
        assertEquals("Titulo original", task.getTitle());
        assertEquals("Descricao original", task.getDescription());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }
}