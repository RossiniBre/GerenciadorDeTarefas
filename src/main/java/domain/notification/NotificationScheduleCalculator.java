package domain.notification;

import domain.model.Task;
import domain.model.TaskPriority;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationScheduleCalculator {

    private final Clock clock;

    public NotificationScheduleCalculator(Clock clock) {
        this.clock = clock;
    }

    public List<Notification> calculate(Task task) {
        List<Notification> notifications = new ArrayList<>();
        LocalDateTime dueDate = task.getDueDate();

        if (dueDate == null) {
            return notifications;
        }

        LocalDateTime now = LocalDateTime.now(clock);

        addIfFuture(notifications, task, NotificationType.DUE_MIDNIGHT, dueDate.toLocalDate().atStartOfDay(), now);

        if (task.getPriority() == TaskPriority.MEDIUM || task.getPriority() == TaskPriority.HIGH) {
            addIfFuture(notifications, task, NotificationType.DUE_THREE_HOURS_BEFORE, dueDate.minusHours(3), now);
        }

        if (task.getPriority() == TaskPriority.HIGH) {
            addIfFuture(notifications, task, NotificationType.DUE_ONE_HOUR_BEFORE, dueDate.minusHours(1), now);
        }

        LocalDateTime overdueTime = dueDate.plusHours(24);
        LocalDateTime earliestOverdue = now.plusHours(24);
        if (overdueTime.isBefore(earliestOverdue)) {
            overdueTime = earliestOverdue;
        }

        notifications.add(Notification.schedule(task.getId(), task.getOwnerId(), NotificationType.OVERDUE, overdueTime));

        return notifications;
    }

    private void addIfFuture(List<Notification> notifications, Task task, NotificationType type, LocalDateTime scheduledFor, LocalDateTime now) {
        if (scheduledFor.isAfter(now)) {
            notifications.add(Notification.schedule(task.getId(), task.getOwnerId(), type, scheduledFor));
        }
    }
}