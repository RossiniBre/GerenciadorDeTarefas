package com.taskmanager.domain.repositories;

import com.taskmanager.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    List<Task> findAllByOwner(String ownerId);
    Optional<Task> findById(String id);
    void removeTask(String id);
}
