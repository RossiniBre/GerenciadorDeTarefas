package application;

import com.taskmanager.application.SendNotificationUseCase;
import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.TaskPriority;
import com.taskmanager.domain.notification.Notification;
import com.taskmanager.domain.notification.NotificationStatus;
import com.taskmanager.domain.notification.NotificationType;
import infrastructure.FakeNotificationSender;
import com.taskmanager.infrastructure.persistence.InMemoryNotificationRepository;
import com.taskmanager.infrastructure.persistence.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SendNotificationUseCaseTest {

    @Test
    void pendingTaskWithDueMidnightNotification_sendsAndMarksAsSent() {
        // Arrange
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Estudar Java", "desc", "owner-1", dueDate, null);
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.DUE_MIDNIGHT, dueDate.toLocalDate().atStartOfDay());
        notificationRepository.save(notification);

        // Act
        useCase.execute(notification);

        // Assert
        assertEquals(1, sender.sentNotifications.size());
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertTrue(sender.sentMessages.get(0).contains("Estudar Java"));
    }

    @Test
    void dueMidnightNotification_sentSuccessfully_doesNotGenerateAnotherOne() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Estudar Java", "desc", "owner-1", dueDate, null);
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.DUE_MIDNIGHT, dueDate.toLocalDate().atStartOfDay());
        notificationRepository.save(notification);

        useCase.execute(notification);

        List<Notification> all = notificationRepository.findByTaskId(task.getId());
        assertEquals(1, all.size());
    }

    @Test
    void dueThreeHoursBeforeNotification_sentSuccessfully_doesNotGenerateAnotherOne() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa media prioridade", "desc", "owner-1", dueDate, null);
        task.updatePriority(TaskPriority.MEDIUM);
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.DUE_THREE_HOURS_BEFORE, dueDate.minusHours(3));
        notificationRepository.save(notification);

        useCase.execute(notification);

        List<Notification> all = notificationRepository.findByTaskId(task.getId());
        assertEquals(1, all.size());
        assertTrue(sender.sentMessages.get(0).contains("3 horas"));
    }

    @Test
    void overdueNotification_taskStillPending_sendsNotStartedMessage() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa atrasada", "desc", "owner-1", dueDate, null);
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.OVERDUE, dueDate.plusHours(24));
        notificationRepository.save(notification);

        useCase.execute(notification);

        assertEquals(1, sender.sentNotifications.size());
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertTrue(sender.sentMessages.get(0).contains("não iniciou"));
    }

    @Test
    void overdueNotification_taskInProgress_sendsInProgressMessage() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa em andamento", "desc", "owner-1", dueDate, null);
        task.startTask();
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.OVERDUE, dueDate.plusHours(24));
        notificationRepository.save(notification);

        useCase.execute(notification);

        assertEquals(1, sender.sentNotifications.size());
        assertTrue(sender.sentMessages.get(0).contains("iniciou"));
        assertFalse(sender.sentMessages.get(0).contains("não iniciou"));
    }

    @Test
    void overdueNotification_sentSuccessfully_schedulesNextOverdueOneDayLater() {
        // Arrange
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa atrasada ha varios dias", "desc", "owner-1", dueDate, null);
        taskRepository.save(task);

        LocalDateTime firstOverdueTime = LocalDateTime.of(2026, 8, 11, 15, 0);
        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.OVERDUE, firstOverdueTime);
        notificationRepository.save(notification);

        // Act
        useCase.execute(notification);

        // Assert
        List<Notification> all = notificationRepository.findByTaskId(task.getId());
        assertEquals(2, all.size());

        Notification sentOne = all.stream()
                .filter(n -> n.getId().equals(notification.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(NotificationStatus.SENT, sentOne.getStatus());

        Notification nextOne = all.stream()
                .filter(n -> !n.getId().equals(notification.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(NotificationStatus.PENDING, nextOne.getStatus());
        assertEquals(NotificationType.OVERDUE, nextOne.getType());
        assertEquals(firstOverdueTime.plusDays(1), nextOne.getScheduledFor());
        assertEquals(task.getId(), nextOne.getTaskId());
        assertEquals(task.getOwnerId(), nextOne.getOwnerId());
    }

    @Test
    void overdueNotification_serverWasDownForDays_stillSchedulesNextOverdueOneDayAfterOriginalScheduledTime() {
        // Simula: OVERDUE agendado para 01/08, mas so processado dia 05 (servidor desligado).
        // O proximo OVERDUE deve ser 02/08 (scheduledFor + 1 dia), nao baseado em "agora".
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 7, 31, 15, 0);
        Task task = Task.newTask("Tarefa muito atrasada", "desc", "owner-1", dueDate, null);
        taskRepository.save(task);

        LocalDateTime overdueScheduledFor = LocalDateTime.of(2026, 8, 1, 0, 0);
        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.OVERDUE, overdueScheduledFor);
        notificationRepository.save(notification);

        useCase.execute(notification);

        List<Notification> all = notificationRepository.findByTaskId(task.getId());
        Notification nextOne = all.stream()
                .filter(n -> !n.getId().equals(notification.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(LocalDateTime.of(2026, 8, 2, 0, 0), nextOne.getScheduledFor());
    }

    @Test
    void taskAlreadyCompleted_cancelsOverdueNotificationAndDoesNotScheduleNext() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa concluida", "desc", "owner-1", dueDate, null);
        task.startTask();
        task.completeTask();
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.OVERDUE, dueDate.plusHours(24));
        notificationRepository.save(notification);

        useCase.execute(notification);

        assertTrue(sender.sentNotifications.isEmpty());
        assertEquals(NotificationStatus.CANCELLED, notification.getStatus());

        List<Notification> all = notificationRepository.findByTaskId(task.getId());
        assertEquals(1, all.size());
    }

    @Test
    void taskAlreadyCompleted_cancelsNonOverdueNotificationInsteadOfSending() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa concluida", "desc", "owner-1", dueDate, null);
        task.startTask();
        task.completeTask();
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.DUE_ONE_HOUR_BEFORE, dueDate.minusHours(1));
        notificationRepository.save(notification);

        useCase.execute(notification);

        assertTrue(sender.sentNotifications.isEmpty());
        assertEquals(NotificationStatus.CANCELLED, notification.getStatus());
    }

    @Test
    void taskDoesNotExist_cancelsNotificationInsteadOfSending() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        Notification notification = Notification.schedule("non-existent-task", "owner-1", NotificationType.DUE_MIDNIGHT, LocalDateTime.of(2026, 8, 10, 0, 0));
        notificationRepository.save(notification);

        useCase.execute(notification);

        assertTrue(sender.sentNotifications.isEmpty());
        assertEquals(NotificationStatus.CANCELLED, notification.getStatus());
    }

    @Test
    void senderThrowsException_notificationRemainsPendingAndNoNextOverdueIsScheduled() {
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryNotificationRepository notificationRepository = new InMemoryNotificationRepository();
        FakeNotificationSender sender = new FakeNotificationSender();
        sender.shouldThrow = true;
        SendNotificationUseCase useCase = new SendNotificationUseCase(notificationRepository, taskRepository, sender);

        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 10, 15, 0);
        Task task = Task.newTask("Tarefa com falha no envio", "desc", "owner-1", dueDate, null);
        taskRepository.save(task);

        Notification notification = Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.OVERDUE, dueDate.plusHours(24));
        notificationRepository.save(notification);

        assertThrows(RuntimeException.class, () -> useCase.execute(notification));

        assertEquals(NotificationStatus.PENDING, notification.getStatus());

        List<Notification> all = notificationRepository.findByTaskId(task.getId());
        assertEquals(1, all.size());
    }
}