package com.taskmanager.domain.repositories;

import com.taskmanager.domain.notification.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<Notification> findPendingScheduledBefore(LocalDateTime instant);
    List<Notification> findByTaskId(String taskId);
}