package infrastructure.scheduler;

import application.SendNotificationUseCase;
import domain.notification.Notification;
import domain.repositories.NotificationRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final SendNotificationUseCase sendNotificationUseCase;
    private final Clock clock;
    private final ScheduledExecutorService executor;

    public NotificationScheduler(NotificationRepository notificationRepository,
                                 SendNotificationUseCase sendNotificationUseCase,
                                 Clock clock) {
        this.notificationRepository = notificationRepository;
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.clock = clock;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        executor.scheduleAtFixedRate(this::processPending, 0, 1, TimeUnit.MINUTES);
    }

    public void stop() {
        executor.shutdown();
    }

    private void processPending() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Notification> pending = notificationRepository.findPendingScheduledBefore(now);

        for (Notification notification : pending) {
            try {
                sendNotificationUseCase.execute(notification);
            } catch (Exception e) {
                System.err.println("Falha ao processar notificação " + notification.getId() + ": " + e.getMessage());
            }
        }
    }
}