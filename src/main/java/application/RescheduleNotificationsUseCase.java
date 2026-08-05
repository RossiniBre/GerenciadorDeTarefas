package application;

import domain.model.Task;
import domain.notification.Notification;
import domain.notification.NotificationStatus;
import domain.repositories.NotificationRepository;

import java.util.List;

public class RescheduleNotificationsUseCase {
    private final NotificationRepository notificationRepository;
    private final CreateNotificationUseCase createNotificationUseCase;

    public RescheduleNotificationsUseCase(NotificationRepository notificationRepository,
                                          CreateNotificationUseCase createNotificationUseCase) {
        this.notificationRepository = notificationRepository;
        this.createNotificationUseCase = createNotificationUseCase;
    }

    public List<Notification> execute(Task task) {
        List<Notification> existing = notificationRepository.findByTaskId(task.getId());

        existing.stream()
                .filter(n -> n.getStatus() == NotificationStatus.PENDING)
                .forEach(n -> {
                    n.cancel();
                    notificationRepository.save(n);
                });

        return createNotificationUseCase.execute(task);
    }
}