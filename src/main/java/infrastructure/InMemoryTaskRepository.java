package infrastructure;

import domain.TaskRepository;
import domain.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class InMemoryTaskRepository implements TaskRepository {

    private List<Task> taskList = new ArrayList<>();

    @Override
    public Task save(Task task) {
        taskList.removeIf(t -> t.getId().equals(task.getId()));
        taskList.add(task);
        return task;
    }

    @Override
    public List<Task> findAll() {
        return taskList;
    }

    @Override
    public Optional<Task> findById(String id) {
        for (Task task : taskList){
            if (task.getId().equals(id)){
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeTask(String id){
        taskList.removeIf(task -> task.getId().equals(id));
    }
}
