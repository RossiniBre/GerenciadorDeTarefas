package application;

import domain.model.Task;
import domain.repositories.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class DeleteTaskUseCase {
    private final TaskRepository repo;
    private final CancelNotificationsUseCase cancelNotificationsUseCase;

    public DeleteTaskUseCase(TaskRepository repo, CancelNotificationsUseCase cancelNotificationsUseCase) {
        this.repo = repo;
        this.cancelNotificationsUseCase = cancelNotificationsUseCase;
    }

    public void execute(String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);

        cancelNotificationsUseCase.execute(id);
        repo.removeTask(id);
    }
}