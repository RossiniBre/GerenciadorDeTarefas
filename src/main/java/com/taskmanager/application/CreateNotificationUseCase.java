package com.taskmanager.application;

import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.notification.Notification;
import com.taskmanager.domain.notification.NotificationScheduleCalculator;
import com.taskmanager.domain.repositories.NotificationRepository;

import java.util.List;

public class CreateNotificationUseCase {
    private final NotificationRepository notificationRepository;
    private final NotificationScheduleCalculator scheduleCalculator;

    public CreateNotificationUseCase(NotificationRepository notificationRepository,
                                     NotificationScheduleCalculator scheduleCalculator) {
        this.notificationRepository = notificationRepository;
        this.scheduleCalculator = scheduleCalculator;
    }

    public List<Notification> execute(Task task) {
        List<Notification> notifications = scheduleCalculator.calculate(task);
        notifications.forEach(notificationRepository::save);
        return notifications;
    }
}