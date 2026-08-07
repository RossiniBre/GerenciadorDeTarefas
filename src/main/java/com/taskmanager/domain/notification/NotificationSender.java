package com.taskmanager.domain.notification;

public interface NotificationSender {
    void send(Notification notification, String message);
}