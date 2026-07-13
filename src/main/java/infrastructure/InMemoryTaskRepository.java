package infrastructure;

import domain.TaskRepository;
import domain.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskRepository implements TaskRepository {

    private List<Task> taskList = new ArrayList<>();

    @Override
    public Task save(Task task) {
        taskList.add(task);
        return task;
    }

    @Override
    public List<Task> findAll() {
        return taskList;
    }
}
