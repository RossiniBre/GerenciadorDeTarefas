import domain.repositories.TaskRepository;
import domain.repositories.UserRepository;
import infrastructure.config.DatabaseConfig;
import infrastructure.config.NotificationConfig;
import infrastructure.persistence.mysql.MySqlTaskRepository;
import infrastructure.persistence.mysql.MySqlUserRepository;
import infrastructure.http.ApiServer;
import application.CreateTaskUseCase;
import application.ListTasksUseCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;


public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        DatabaseConfig config = DatabaseConfig.load();
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

        Clock clock = Clock.systemDefaultZone();

        UserRepository userRepository = new MySqlUserRepository(connection);
        TaskRepository taskRepository = new MySqlTaskRepository(connection);

        NotificationConfig notifications = NotificationConfig.build(taskRepository, clock);

        CreateTaskUseCase createTaskUseCase =
                new CreateTaskUseCase(taskRepository, clock, notifications.createNotificationUseCase);
        ListTasksUseCase listTasksUseCase = new ListTasksUseCase(taskRepository);

        notifications.scheduler.start();
        Runtime.getRuntime().addShutdownHook(new Thread(notifications.scheduler::stop));

        ApiServer apiServer = new ApiServer(
                createTaskUseCase,
                listTasksUseCase,
                userRepository,
                taskRepository,
                notifications.rescheduleNotificationsUseCase,
                notifications.cancelNotificationsUseCase,
                notifications.listNotificationsUseCase
        );

        apiServer.start(8080);
    }
}