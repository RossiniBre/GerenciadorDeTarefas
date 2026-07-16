package application;

import domain.Task;
import domain.TaskStatus;
import infrastructure.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StartTaskUseCaseTest {

    @Test
    void shouldStartPendingTask(){
        // Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task existingTask = Task.newTask("Titulo original", "Descricao Original", "owner-123");
        repo.save(existingTask);
        String existingId = existingTask.getId();

        // Act
        StartTaskUseCase useCase= new StartTaskUseCase(repo);
        useCase.execute(existingId, "owner-123");

        // Assert
        TaskStatus status = repo.findById(existingId).orElseThrow().getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, status);

    }
}
