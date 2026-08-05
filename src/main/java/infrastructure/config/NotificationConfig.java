package infrastructure.config;

import application.*;
import domain.notification.NotificationScheduleCalculator;
import domain.notification.NotificationSender;
import domain.repositories.NotificationRepository;
import domain.repositories.TaskRepository;
import domain.notification.ConsoleNotificationSender;
import infrastructure.persistence.InMemoryNotificationRepository;
import infrastructure.scheduler.NotificationScheduler;

import java.time.Clock;

public class NotificationConfig {

    public final CreateNotificationUseCase createNotificationUseCase;
    public final RescheduleNotificationsUseCase rescheduleNotificationsUseCase;
    public final CancelNotificationsUseCase cancelNotificationsUseCase;
    public final ListNotificationsUseCase listNotificationsUseCase;
    public final NotificationScheduler scheduler;

    private NotificationConfig(CreateNotificationUseCase createNotificationUseCase,
                               RescheduleNotificationsUseCase rescheduleNotificationsUseCase,
                               CancelNotificationsUseCase cancelNotificationsUseCase,
                               ListNotificationsUseCase listNotificationsUseCase,
                               NotificationScheduler scheduler) {
        this.createNotificationUseCase = createNotificationUseCase;
        this.rescheduleNotificationsUseCase = rescheduleNotificationsUseCase;
        this.cancelNotificationsUseCase = cancelNotificationsUseCase;
        this.listNotificationsUseCase = listNotificationsUseCase;
        this.scheduler = scheduler;
    }

    public static NotificationConfig build(TaskRepository taskRepository, Clock clock) {
        NotificationRepository notificationRepository = new InMemoryNotificationRepository();
        NotificationScheduleCalculator scheduleCalculator = new NotificationScheduleCalculator(clock);
        NotificationSender notificationSender = new ConsoleNotificationSender();

        CreateNotificationUseCase createNotificationUseCase =
                new CreateNotificationUseCase(notificationRepository, scheduleCalculator);
        RescheduleNotificationsUseCase rescheduleNotificationsUseCase =
                new RescheduleNotificationsUseCase(notificationRepository, createNotificationUseCase);
        CancelNotificationsUseCase cancelNotificationsUseCase =
                new CancelNotificationsUseCase(notificationRepository);
        ListNotificationsUseCase listNotificationsUseCase =
                new ListNotificationsUseCase(notificationRepository);
        SendNotificationUseCase sendNotificationUseCase =
                new SendNotificationUseCase(notificationRepository, taskRepository, notificationSender);
        NotificationScheduler scheduler =
                new NotificationScheduler(notificationRepository, sendNotificationUseCase, clock);

        return new NotificationConfig(createNotificationUseCase, rescheduleNotificationsUseCase,
                cancelNotificationsUseCase, listNotificationsUseCase, scheduler);
    }
}