import domain.Task;
import application.CreateTaskUseCase;
import infrastructure.InMemoryTaskRepository;
import domain.TaskRepository;

public class Main {
    public static void main(String [] args){
        TaskRepository repo = new InMemoryTaskRepository();
        CreateTaskUseCase useCase = new CreateTaskUseCase(repo);
        Task t = useCase.execute("Limpar a casa", "Banheiros, Quartos e Sala");
        System.out.println(t);
    }
}
