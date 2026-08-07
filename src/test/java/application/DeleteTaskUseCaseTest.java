package application;

import com.taskmanager.application.CancelNotificationsUseCase;
import com.taskmanager.application.DeleteTaskUseCase;
import com.taskmanager.domain.model.Task;
import com.taskmanager.infrastructure.persistence.InMemoryNotificationRepository;
import com.taskmanager.infrastructure.persistence.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteTaskUseCaseTest {

    InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
    CancelNotificationsUseCase cancelNotificationsUseCase = new CancelNotificationsUseCase(notificationRepository);

    @Test
    void shouldDeleteTask(){
        //Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task existingTask = Task.newTask("Titulo original", "Descricao Original", "owner-123", null, null);
        repo.save(existingTask);
        String existingId = existingTask.getId();

        DeleteTaskUseCase useCase = new DeleteTaskUseCase(repo, cancelNotificationsUseCase);

        // Act
        useCase.execute(existingId, "owner-123");

        // Assert
        assertEquals(0, repo.findAllByOwner("owner-123").size());
    }
}