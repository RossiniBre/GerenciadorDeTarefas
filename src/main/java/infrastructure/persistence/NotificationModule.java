package infrastructure.persistence;

import application.*;
import domain.notification.NotificationScheduleCalculator;
import domain.notification.NotificationSender;
import domain.repositories.NotificationRepository;
import domain.repositories.TaskRepository;
import domain.notification.ConsoleNotificationSender;
import infrastructure.scheduler.NotificationScheduler;

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