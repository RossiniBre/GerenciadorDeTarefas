package com.taskmanager.infrastructure.persistence;

import com.taskmanager.application.CancelNotificationsUseCase;
import com.taskmanager.application.CreateNotificationUseCase;
import com.taskmanager.application.RescheduleNotificationsUseCase;
import com.taskmanager.application.SendNotificationUseCase;
import com.taskmanager.domain.notification.NotificationScheduleCalculator;
import com.taskmanager.domain.notification.NotificationSender;
import com.taskmanager.domain.repositories.NotificationRepository;
import com.taskmanager.domain.repositories.TaskRepository;
import com.taskmanager.domain.notification.ConsoleNotificationSender;
import com.taskmanager.domain.notification.NotificationScheduler;

import java.time.Clock;

public class NotificationModule {

    public final CreateNotificationUseCase createNotificationUseCase;
    public final RescheduleNotificationsUseCase rescheduleNotificationsUseCase;
    public final CancelNotificationsUseCase cancelNotificationsUseCase;
    public final SendNotificationUseCase sendNotificationUseCase;
    public final NotificationScheduler notificationScheduler;

    public NotificationModule(TaskRepository taskRepository, Clock clock) {
        NotificationRepository notificationRepository = new InMemoryNotificationRepository();
        NotificationScheduleCalculator scheduleCalculator = new NotificationScheduleCalculator(clock);
        NotificationSender notificationSender = new ConsoleNotificationSender();

        this.createNotificationUseCase =
                new CreateNotificationUseCase(notificationRepository, scheduleCalculator);

        this.rescheduleNotificationsUseCase =
                new RescheduleNotificationsUseCase(notificationRepository, createNotificationUseCase);

        this.cancelNotificationsUseCase =
                new CancelNotificationsUseCase(notificationRepository);

        this.sendNotificationUseCase =
                new SendNotificationUseCase(notificationRepository, taskRepository, notificationSender);

        this.notificationScheduler =
                new NotificationScheduler(notificationRepository, sendNotificationUseCase, clock);
    }
}