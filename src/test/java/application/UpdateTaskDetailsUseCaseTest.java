package application;

import com.taskmanager.application.CancelNotificationsUseCase;
import com.taskmanager.application.CreateNotificationUseCase;
import com.taskmanager.application.RescheduleNotificationsUseCase;
import com.taskmanager.application.UpdateTaskDetailsUseCase;
import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.notification.NotificationScheduleCalculator;
import com.taskmanager.infrastructure.persistence.InMemoryNotificationRepository;
import com.taskmanager.infrastructure.persistence.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class UpdateTaskDetailsUseCaseTest {

    InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
    CancelNotificationsUseCase cancelNotificationsUseCase = new CancelNotificationsUseCase(notificationRepository);

    @Test
    void shouldUpdateTitleAndDescription() {
        // Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task existingTask = Task.newTask("Titulo antigo", "Descricao antiga", "owner-123", null, null);
        repo.save(existingTask);
        String existingId = existingTask.getId();

        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-04T10:00:00Z"), ZoneOffset.UTC);

        NotificationScheduleCalculator scheduleCalculator = new NotificationScheduleCalculator(fixedClock);
        CreateNotificationUseCase createNotificationUseCase =
                new CreateNotificationUseCase(notificationRepository, scheduleCalculator);
        RescheduleNotificationsUseCase rescheduleNotificationsUseCase =
                new RescheduleNotificationsUseCase(notificationRepository, createNotificationUseCase);

        UpdateTaskDetailsUseCase useCase = new UpdateTaskDetailsUseCase(repo, fixedClock, rescheduleNotificationsUseCase);
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 20, 18, 0);
        LocalDateTime reminderDate = LocalDateTime.of(2026, 8, 19, 9, 0);

        // Act
        Task updatedTask = useCase.execute(
                "Novo Titulo", "Nova Descricao",
                null, null,           // priority, category — não alterados
                dueDate, reminderDate,
                existingId, "owner-123"
        );

        // Assert
        assertEquals("Novo Titulo", updatedTask.getTitle());
        assertEquals("Nova Descricao", updatedTask.getDescription());
        assertEquals(existingTask.getId(), updatedTask.getId());
        assertEquals(dueDate, updatedTask.getDueDate());
        assertEquals(reminderDate, updatedTask.getReminderDate());
    }
}