package com.taskmanager.application;

import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.User;
import com.taskmanager.domain.repositories.TaskRepository;
import com.taskmanager.domain.exceptions.TaskNotFoundException;

public class CompleteTaskUseCase {
    private final TaskRepository repo;
    private final CancelNotificationsUseCase cancelNotificationsUseCase;

    public CompleteTaskUseCase(TaskRepository repo, CancelNotificationsUseCase cancelNotificationsUseCase) {
        this.repo = repo;
        this.cancelNotificationsUseCase = cancelNotificationsUseCase;
    }

    public Task execute(String taskId, User loggedUser) {
        Task task = repo.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        task.verifyOwnership(loggedUser.getId());
        task.completeTask();

        Task savedTask = repo.save(task);

        cancelNotificationsUseCase.execute(taskId);

        return savedTask;
    }
}