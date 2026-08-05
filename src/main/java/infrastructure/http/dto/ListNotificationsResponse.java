package infrastructure.http.dto;

import domain.notification.Notification;

import java.util.List;

public record ListNotificationsResponse(List<NotificationItem> notifications) {

    public record NotificationItem(
            String id,
            String taskId,
            String type,
            String scheduledFor,
            String status
    ) {
        public static NotificationItem from(Notification notification) {
            return new NotificationItem(
                    notification.getId(),
                    notification.getTaskId(),
                    notification.getType().name(),
                    notification.getScheduledFor().toString(),
                    notification.getStatus().name()
            );
        }
    }
}