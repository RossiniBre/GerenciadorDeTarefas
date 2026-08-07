package com.taskmanager.domain.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private final String id;
    private final String taskId;
    private final String ownerId;
    private final NotificationType type;
    private final LocalDateTime scheduledFor;
    private NotificationStatus status;

    private Notification(String id, String taskId, String ownerId, NotificationType type,
                         LocalDateTime scheduledFor, NotificationStatus status) {
        this.id = id;
        this.taskId = taskId;
        this.ownerId = ownerId;
        this.type = type;
        this.scheduledFor = scheduledFor;
        this.status = status;
    }

    public static Notification schedule(String taskId, String ownerId, NotificationType type, LocalDateTime scheduledFor) {
        return new Notification(UUID.randomUUID().toString(), taskId, ownerId, type, scheduledFor, NotificationStatus.PENDING);
    }

    public static Notification rebuild(String id, String taskId, String ownerId, NotificationType type,
                                       LocalDateTime scheduledFor, NotificationStatus status) {
        return new Notification(id, taskId, ownerId, type, scheduledFor, status);
    }

    public void markSent() {
        if (status != NotificationStatus.PENDING) {
            throw new IllegalStateException("Só é possível enviar uma notificação pendente!");
        }
        this.status = NotificationStatus.SENT;
    }

    public void cancel() {
        if (status != NotificationStatus.PENDING) {
            throw new IllegalStateException("Só é possível cancelar uma notificação pendente!");
        }
        this.status = NotificationStatus.CANCELLED;
    }

    public String getId() { return id; }
    public String getTaskId() { return taskId; }
    public String getOwnerId() { return ownerId; }
    public NotificationType getType() { return type; }
    public LocalDateTime getScheduledFor() { return scheduledFor; }
    public NotificationStatus getStatus() { return status; }
}