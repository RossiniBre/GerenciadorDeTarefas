import domain.*;
import application.CreateTaskUseCase;
import infrastructure.InMemoryTaskRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskRepository repo = new InMemoryTaskRepository();
        CreateTaskUseCase useCase = new CreateTaskUseCase(repo);

        User user = User.newUser("breno", "123");

        Task t = useCase.execute("Estudar Java", "Revisar Fase 4", user);

        System.out.println(t);
        List<Task> tasksDoUser = repo.findAllByOwner(user.getId());
        for (Task task : tasksDoUser) {
            System.out.println("- " + task.getTitle());
        }
    }
}