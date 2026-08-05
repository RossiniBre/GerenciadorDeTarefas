package application;

import domain.notification.Notification;
import domain.notification.NotificationScheduleCalculator;
import domain.notification.NotificationStatus;
import domain.model.Task;
import domain.model.TaskPriority;
import domain.repositories.NotificationRepository;
import infrastructure.persistence.InMemoryNotificationRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CancelNotificationsUseCaseTest {

    private final ZoneId zone = ZoneId.systemDefault();
    private final Clock fixedClock = Clock.fixed(
            LocalDateTime.of(2026, 8, 4, 10, 0).atZone(zone).toInstant(),
            zone
    );

    private final NotificationRepository notificationRepository = new InMemoryNotificationRepository();
    private final NotificationScheduleCalculator scheduleCalculator = new NotificationScheduleCalculator(fixedClock);
    private final CreateNotificationUseCase createNotificationUseCase =
            new CreateNotificationUseCase(notificationRepository, scheduleCalculator);
    private final CancelNotificationsUseCase useCase =
            new CancelNotificationsUseCase(notificationRepository);

    @Test
    void taskWithPendingNotifications_cancelsAllOfThem() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa a deletar", "desc", "owner-1", dueDate, null);
        task.updatePriority(TaskPriority.HIGH);

        createNotificationUseCase.execute(task);

        useCase.execute(task.getId());

        List<Notification> all = notificationRepository.findByTaskId(task.getId());
        assertTrue(all.stream().allMatch(n -> n.getStatus() == NotificationStatus.CANCELLED));
        assertEquals(4, all.size());
    }

    @Test
    void taskWithNoNotifications_doesNothingAndDoesNotThrow() {
        useCase.execute("non-existent-task-id");
        List<Notification> all = notificationRepository.findByTaskId("non-existent-task-id");
        assertTrue(all.isEmpty());
    }

    @Test
    void alreadyCancelledOrSentNotification_isSkipped() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa", "desc", "owner-1", dueDate, null);

        List<Notification> created = createNotificationUseCase.execute(task);
        Notification alreadySent = created.get(0);
        alreadySent.markSent();
        notificationRepository.save(alreadySent);

        useCase.execute(task.getId());

        Notification stillSent = notificationRepository.findByTaskId(task.getId()).stream()
                .filter(n -> n.getId().equals(alreadySent.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(NotificationStatus.SENT, stillSent.getStatus());
    }
}