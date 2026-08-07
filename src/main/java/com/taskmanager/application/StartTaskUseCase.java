package com.taskmanager.application;

import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.User;
import com.taskmanager.domain.repositories.TaskRepository;
import com.taskmanager.domain.exceptions.TaskNotFoundException;

public class StartTaskUseCase {
    private final TaskRepository repo;

    public StartTaskUseCase(TaskRepository repo) { this.repo = repo; }

    public Task execute(String taskId, User loggedUser) {
        Task task = repo.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        task.verifyOwnership(loggedUser.getId());
        task.startTask();
        return repo.save(task);
    }
}