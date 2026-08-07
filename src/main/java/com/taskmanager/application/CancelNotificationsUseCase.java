package com.taskmanager.application;

import com.taskmanager.domain.notification.NotificationStatus;
import com.taskmanager.domain.repositories.NotificationRepository;

public class CancelNotificationsUseCase {
    private final NotificationRepository notificationRepository;

    public CancelNotificationsUseCase(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void execute(String taskId) {
        notificationRepository.findByTaskId(taskId).stream()
                .filter(n -> n.getStatus() == NotificationStatus.PENDING)
                .forEach(n -> {
                    n.cancel();
                    notificationRepository.save(n);
                });
    }
}