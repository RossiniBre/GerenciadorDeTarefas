import domain.*;
import application.CreateTaskUseCase;
import infrastructure.InMemoryTaskRepository;

public class Main {
    public static void main(String [] args){
        TaskRepository repo = new InMemoryTaskRepository();
        CreateTaskUseCase useCase = new CreateTaskUseCase(repo);
        Task t = new TaskBuilder()
                .title("Estudar Java")
                .priority(TaskPriority.HIGH)
                .category(TaskCategory.STUDY)
                .build();
        System.out.println(t);
    }
}
