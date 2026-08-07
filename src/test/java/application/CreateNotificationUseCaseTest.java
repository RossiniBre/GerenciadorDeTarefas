package application;

import com.taskmanager.application.CreateNotificationUseCase;
import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.TaskPriority;
import com.taskmanager.domain.notification.Notification;
import com.taskmanager.domain.notification.NotificationScheduleCalculator;
import com.taskmanager.domain.notification.NotificationType;
import com.taskmanager.domain.repositories.NotificationRepository;
import com.taskmanager.infrastructure.persistence.InMemoryNotificationRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateNotificationUseCaseTest {

    private final ZoneId zone = ZoneId.systemDefault();
    private final Clock fixedClock = Clock.fixed(
            LocalDateTime.of(2026, 8, 4, 10, 0).atZone(zone).toInstant(),
            zone
    );

    private final NotificationRepository notificationRepository = new InMemoryNotificationRepository();
    private final NotificationScheduleCalculator scheduleCalculator = new NotificationScheduleCalculator(fixedClock);
    private final CreateNotificationUseCase useCase =
            new CreateNotificationUseCase(notificationRepository, scheduleCalculator);

    @Test
    void taskWithHighPriorityAndFutureDueDate_createsAllNotificationsAndPersistsThem() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Estudar Java", "desc", "owner-1", dueDate, null);
        task.updatePriority(TaskPriority.HIGH);

        List<Notification> result = useCase.execute(task);

        assertEquals(4, result.size());
        assertEquals(4, notificationRepository.findByTaskId(task.getId()).size());
    }

    @Test
    void taskWithoutDueDate_createsNoNotifications() {
        Task task = Task.newTask("Sem prazo", "desc", "owner-1", null, null);

        List<Notification> result = useCase.execute(task);

        assertTrue(result.isEmpty());
        assertTrue(notificationRepository.findByTaskId(task.getId()).isEmpty());
    }

    @Test
    void createdNotifications_haveCorrectTaskIdAndOwnerId() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Estudar Java", "desc", "owner-1", dueDate, null);

        List<Notification> result = useCase.execute(task);

        for (Notification notification : result) {
            assertEquals(task.getId(), notification.getTaskId());
            assertEquals("owner-1", notification.getOwnerId());
        }
    }

    @Test
    void lowPriorityTask_createsOnlyMidnightAndOverdueNotifications() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Baixa prioridade", "desc", "owner-1", dueDate, null);
        task.updatePriority(TaskPriority.LOW);

        List<Notification> result = useCase.execute(task);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(n -> n.getType() == NotificationType.DUE_MIDNIGHT));
        assertTrue(result.stream().anyMatch(n -> n.getType() == NotificationType.OVERDUE));
    }

    @Test
    void taskWithDueDateAlreadyPastByMoreThan24Hours_schedulesOverdueForNowPlus24Hours() {
        // Arrange
        LocalDateTime dueDateAlreadyOverdue = LocalDateTime.of(2026, 8, 1, 10, 0);
        Task task = Task.newTask("Tarefa reagendada para prazo ainda passado", "desc", "owner-1", dueDateAlreadyOverdue, null);

        // Act
        List<Notification> result = useCase.execute(task);

        // Assert
        assertEquals(1, result.size());

        Notification overdue = result.get(0);
        assertEquals(NotificationType.OVERDUE, overdue.getType());

        LocalDateTime now = LocalDateTime.now(fixedClock);
        assertEquals(now.plusHours(24), overdue.getScheduledFor());
    }
}