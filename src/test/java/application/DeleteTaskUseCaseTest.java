package application;

import domain.Task;
import infrastructure.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteTaskUseCaseTest {

    @Test
    void shouldDeleteTask(){
        //Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task existingTask = Task.newTask("Titulo original", "Descricao Original");
        repo.save(existingTask);
        String existingId = existingTask.getId();

        DeleteTaskUseCase useCase = new DeleteTaskUseCase(repo);

        // Act
        useCase.execute(existingId);

        // Assert
        assertEquals(0, repo.findAll().size());
    }
}
