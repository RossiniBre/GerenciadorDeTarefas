package application;

import domain.model.Task;
import domain.model.TaskStatus;
import domain.notification.Notification;
import domain.notification.NotificationType;
import domain.notification.NotificationSender;
import domain.repositories.NotificationRepository;
import domain.repositories.TaskRepository;

import java.util.Optional;

public class SendNotificationUseCase {
    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;
    private final NotificationSender sender;

    public SendNotificationUseCase(NotificationRepository notificationRepository,
                                   TaskRepository taskRepository,
                                   NotificationSender sender) {
        this.notificationRepository = notificationRepository;
        this.taskRepository = taskRepository;
        this.sender = sender;
    }

    public void execute(Notification notification) {
        Optional<Task> maybeTask = taskRepository.findById(notification.getTaskId());

        if (maybeTask.isEmpty()) {
            notification.cancel();
            notificationRepository.save(notification);
            return;
        }

        Task task = maybeTask.get();

        if (task.getStatus() == TaskStatus.COMPLETED) {
            notification.cancel();
            notificationRepository.save(notification);
            return;
        }

        String message = buildMessage(notification, task);

        sender.send(notification, message);

        notification.markSent();
        notificationRepository.save(notification);

        if (notification.getType() == NotificationType.OVERDUE) {
            scheduleNextOverdue(notification, task);
        }
    }

    private void scheduleNextOverdue(Notification sentOverdue, Task task) {
        Notification next = Notification.schedule(
                task.getId(),
                task.getOwnerId(),
                NotificationType.OVERDUE,
                sentOverdue.getScheduledFor().plusDays(1)
        );
        notificationRepository.save(next);
    }

    private String buildMessage(Notification notification, Task task) {
        NotificationType type = notification.getType();

        return switch (type) {
            case DUE_MIDNIGHT -> "A tarefa \"" + task.getTitle() + "\" vence hoje.";
            case DUE_THREE_HOURS_BEFORE -> "A tarefa \"" + task.getTitle() + "\" vence em 3 horas.";
            case DUE_ONE_HOUR_BEFORE -> "A tarefa \"" + task.getTitle() + "\" vence em 1 hora.";
            case OVERDUE -> buildOverdueMessage(task);
        };
    }

    private String buildOverdueMessage(Task task) {
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            return "Você iniciou a tarefa \"" + task.getTitle() + "\", mas ela venceu e ainda não foi concluída.";
        }
        return "Você não iniciou a tarefa \"" + task.getTitle() + "\", que venceu. Verifique suas tarefas quando puder.";
    }
}