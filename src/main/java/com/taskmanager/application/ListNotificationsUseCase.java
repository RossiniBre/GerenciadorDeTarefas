package com.taskmanager.application;

import com.taskmanager.domain.notification.Notification;
import com.taskmanager.domain.repositories.NotificationRepository;

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