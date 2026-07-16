package application;

import domain.Task;
import infrastructure.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateTaskDetailsUseCaseTest {

    @Test
    void shouldUpdateTitleAndDescription() {
        // Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task existingTask = Task.newTask("Titulo antigo", "Descricao antiga", "owner-123");
        repo.save(existingTask);
        String existingId = existingTask.getId();

        UpdateTaskDetailsUseCase useCase = new UpdateTaskDetailsUseCase(repo);

        // Act
        Task updatedTask = useCase.execute("Novo Titulo", "Nova Descricao", existingId, "owner-123");

        // Assert
       assertEquals("Novo Titulo", updatedTask.getTitle());
       assertEquals("Nova Descricao", updatedTask.getDescription());
       assertEquals(existingTask.getId(), updatedTask.getId());
    }
}