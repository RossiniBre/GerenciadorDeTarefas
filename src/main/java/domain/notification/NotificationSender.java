package domain.notification;

public interface NotificationSender {
    void send(Notification notification, String message);
}