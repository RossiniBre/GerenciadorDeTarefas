package infrastructure.persistence;

import domain.notification.Notification;
import domain.notification.NotificationStatus;
import domain.repositories.NotificationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InMemoryNotificationRepository implements NotificationRepository {

    private List<Notification> notificationList = new ArrayList<>();

    @Override
    public Notification save(Notification notification) {
        notificationList.removeIf(n -> n.getId().equals(notification.getId()));
        notificationList.add(notification);
        return notification;
    }

    @Override
    public List<Notification> findPendingScheduledBefore(LocalDateTime instant) {
        List<Notification> result = new ArrayList<>();
        for (Notification notification : notificationList) {
            if (notification.getStatus() == NotificationStatus.PENDING
                    && !notification.getScheduledFor().isAfter(instant)) {
                result.add(notification);
            }
        }
        return result;
    }

    @Override
    public List<Notification> findByTaskId(String taskId) {
        List<Notification> result = new ArrayList<>();
        for (Notification notification : notificationList) {
            if (notification.getTaskId().equals(taskId)) {
                result.add(notification);
            }
        }
        return result;
    }
}