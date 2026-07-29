package application;

import application.usecases.CompleteTaskUseCase;
import domain.model.Task;
import domain.model.TaskStatus;
import domain.model.User;
import infrastructure.persistence.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompleteTaskUseCaseTest {

    @Test
    void shouldCompleteInProgressTask(){
        // Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();

        User user = User.newUser("owner123", "senhaHash");

        Task existingTask = Task.newTask(
                "Titulo original",
                "Descricao Original",
                user.getId()
        );

        existingTask.startTask();

        repo.save(existingTask);

        String existingId = existingTask.getId();

        // Act
        CompleteTaskUseCase useCase = new CompleteTaskUseCase(repo);
        useCase.execute(existingId, user);

        // Assert
        TaskStatus status = repo.findById(existingId)
                .orElseThrow()
                .getStatus();

        assertEquals(TaskStatus.COMPLETED, status);
    }
}