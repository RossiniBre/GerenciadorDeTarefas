package application;

import domain.Task;
import domain.TaskStatus;
import infrastructure.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompleteTaskUseCaseTest {

    private static final String OWNER_ID = "owner-123";

    @Test
    void shouldCompleteInProgressTask(){
        // Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task existingTask = Task.newTask("Titulo original", "Descricao Original", OWNER_ID);
        existingTask.startTask();
        repo.save(existingTask);
        String existingId = existingTask.getId();

        // Act
        CompleteTaskUseCase useCase = new CompleteTaskUseCase(repo);
        useCase.execute(existingId, OWNER_ID);

        // Assert
        TaskStatus status = repo.findById(existingId).orElseThrow().getStatus();
        assertEquals(TaskStatus.COMPLETED, status);
    }
}