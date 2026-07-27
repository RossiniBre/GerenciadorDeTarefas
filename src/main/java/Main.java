import domain.TaskRepository;
import domain.UserRepository;
import infrastructure.DatabaseConfig;
import infrastructure.MySqlTaskRepository;
import infrastructure.MySqlUserRepository;
import infrastructure.http.ApiServer;
import application.CreateTaskUseCase;
import application.ListTasksUseCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        DatabaseConfig config = DatabaseConfig.load();
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

        UserRepository userRepository = new MySqlUserRepository(connection);
        TaskRepository taskRepository = new MySqlTaskRepository(connection);

        CreateTaskUseCase createTaskUseCase = new CreateTaskUseCase(taskRepository);
        ListTasksUseCase listTasksUseCase = new ListTasksUseCase(taskRepository);

        ApiServer apiServer = new ApiServer(createTaskUseCase, listTasksUseCase, userRepository, taskRepository);
        apiServer.start(8080);
    }
}