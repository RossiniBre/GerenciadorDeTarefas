package application;

import domain.notification.Notification;
import domain.repositories.NotificationRepository;

import java.util.List;

public class ListNotificationsUseCase {
    private final NotificationRepository notificationRepository;

    public ListNotificationsUseCase(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> execute(String taskId) {
        return notificationRepository.findByTaskId(taskId);
    }
}