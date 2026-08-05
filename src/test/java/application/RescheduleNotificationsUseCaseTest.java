package application;

import domain.model.Task;
import domain.model.TaskPriority;
import domain.notification.Notification;
import domain.notification.NotificationScheduleCalculator;
import domain.notification.NotificationStatus;
import domain.repositories.NotificationRepository;
import infrastructure.persistence.InMemoryNotificationRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RescheduleNotificationsUseCaseTest {

    private final ZoneId zone = ZoneId.systemDefault();
    private final Clock fixedClock = Clock.fixed(
            LocalDateTime.of(2026, 8, 4, 10, 0).atZone(zone).toInstant(),
            zone
    );

    private final NotificationRepository notificationRepository = new InMemoryNotificationRepository();
    private final NotificationScheduleCalculator scheduleCalculator = new NotificationScheduleCalculator(fixedClock);
    private final CreateNotificationUseCase createNotificationUseCase =
            new CreateNotificationUseCase(notificationRepository, scheduleCalculator);
    private final RescheduleNotificationsUseCase useCase =
            new RescheduleNotificationsUseCase(notificationRepository, createNotificationUseCase);

    @Test
    void taskWithExistingPendingNotifications_cancelsOldOnesAndCreatesNewOnes() {
        // Arrange
        LocalDateTime originalDueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Estudar Java", "desc", "owner-1", originalDueDate, null);
        task.updatePriority(TaskPriority.HIGH);

        List<Notification> original = createNotificationUseCase.execute(task);
        List<String> originalIds = original.stream().map(Notification::getId).toList();

        // Act
        LocalDateTime newDueDate = LocalDateTime.of(2026, 8, 15, 15, 0);
        task.updateDueDate(newDueDate);
        List<Notification> rescheduled = useCase.execute(task);

        // Assert
        for (String id : originalIds) {
            Notification cancelled = notificationRepository.findByTaskId(task.getId()).stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst()
                    .orElseThrow();
            assertEquals(NotificationStatus.CANCELLED, cancelled.getStatus());
        }

        assertTrue(rescheduled.stream().allMatch(n -> n.getStatus() == NotificationStatus.PENDING));
        assertEquals(4, rescheduled.size());
    }

    @Test
    void taskWithNoExistingNotifications_justCreatesNewOnes() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Sem notificacao previa", "desc", "owner-1", dueDate, null);

        List<Notification> result = useCase.execute(task);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(n -> n.getStatus() == NotificationStatus.PENDING));
    }

    @Test
    void alreadySentNotification_isNotCancelledOnReschedule() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa", "desc", "owner-1", dueDate, null);
        task.updatePriority(TaskPriority.HIGH);

        List<Notification> original = createNotificationUseCase.execute(task);
        Notification alreadySent = original.get(0);
        alreadySent.markSent();
        notificationRepository.save(alreadySent);

        useCase.execute(task);

        Notification stillSent = notificationRepository.findByTaskId(task.getId()).stream()
                .filter(n -> n.getId().equals(alreadySent.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(NotificationStatus.SENT, stillSent.getStatus());
    }
}