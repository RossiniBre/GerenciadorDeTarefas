package application;

import domain.Task;
import domain.TaskStatus;
import domain.User;
import infrastructure.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateTaskUseCaseTest {

    @Test
    void shouldCreateTaskWithPendingStatus() {
        // Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        CreateTaskUseCase useCase = new CreateTaskUseCase(repo);
        // Act
        User owner = User.newUser("owner-123", "hash-fake-123");
        Task task = useCase.execute("Titulo original", "Descricao original", owner, null, null);

        // Assert
        assertEquals("Titulo original", task.getTitle());
        assertEquals("Descricao original", task.getDescription());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }
}