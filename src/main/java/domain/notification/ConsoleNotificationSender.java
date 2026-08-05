package domain.notification;

public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification, String message) {
        System.out.println("🔔 [" + notification.getType() + "] " + message);
    }
}