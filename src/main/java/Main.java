import application.CreateTaskUseCase;
import domain.Task;
import domain.TaskRepository;
import domain.User;
import infrastructure.MySqlTaskRepository;
import infrastructure.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConfig config = DatabaseConfig.load();
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

        TaskRepository sqlRepo = new MySqlTaskRepository(connection);
        CreateTaskUseCase useCase = new CreateTaskUseCase(sqlRepo);
        
        User fakeUser = new User("teste-user-id", "usuarioTeste", "hashFake");

        Task t = useCase.execute("Estudar Java", "Testar conexão MySQL", fakeUser);
        System.out.println("Task criada: " + t);

        List<Task> tasks = sqlRepo.findAllByOwner(fakeUser.getId());
        for (Task task : tasks) {
            System.out.println("- " + task.getTitle());
        }
    }
}