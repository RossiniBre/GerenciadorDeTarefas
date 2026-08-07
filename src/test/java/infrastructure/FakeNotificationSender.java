package infrastructure;

import com.taskmanager.domain.notification.Notification;
import com.taskmanager.domain.notification.NotificationSender;

import java.util.ArrayList;
import java.util.List;

public class FakeNotificationSender implements NotificationSender {

    public final List<Notification> sentNotifications = new ArrayList<>();
    public final List<String> sentMessages = new ArrayList<>();
    public boolean shouldThrow = false;

    @Override
    public void send(Notification notification, String message) {
        if (shouldThrow) {
            throw new RuntimeException("Falha simulada no envio");
        }
        sentNotifications.add(notification);
        sentMessages.add(message);
    }
}