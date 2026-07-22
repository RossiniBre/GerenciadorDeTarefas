import application.CreateTaskUseCase;
import application.ListTasksUseCase;
import application.LoginUseCase;
import application.RegisterUserUseCase;
import domain.Task;
import domain.TaskCategory;
import domain.TaskPriority;
import domain.TaskRepository;
import domain.TaskStatus;
import domain.User;
import domain.UserRepository;
import domain.PasswordHasher;
import infrastructure.DatabaseConfig;
import infrastructure.MySqlTaskRepository;
import infrastructure.MySqlUserRepository;
import infrastructure.Pbkdf2PasswordHasher;
import infrastructure.http.ApiServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        DatabaseConfig config = DatabaseConfig.load();
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

        UserRepository userRepository = new MySqlUserRepository(connection);
        PasswordHasher passwordHasher = new Pbkdf2PasswordHasher();
        TaskRepository taskRepository = new MySqlTaskRepository(connection);
        
        RegisterUserUseCase registerUserUseCase = new RegisterUserUseCase(userRepository, passwordHasher);
        User realUser = registerUserUseCase.execute("usuarioTeste", "senhaSegura123");
        System.out.println("Usuário criado: " + realUser.getId());

        LoginUseCase loginUseCase = new LoginUseCase(userRepository, passwordHasher);
        User loggedUser = loginUseCase.execute("usuarioTeste", "senhaSegura123");
        System.out.println("Login OK: " + loggedUser.getId());

        CreateTaskUseCase createTaskUseCase = new CreateTaskUseCase(taskRepository);

        Task t1 = createTaskUseCase.execute("Terminar relatório", "Fechar números do trimestre", loggedUser, null, null);
        t1.updatePriority(TaskPriority.HIGH);
        t1.updateCategory(TaskCategory.WORK);
        t1.startTask();
        taskRepository.save(t1);

        Task t2 = createTaskUseCase.execute("Comprar mantimentos", "Feira da semana", loggedUser, null, null);
        t2.updatePriority(TaskPriority.LOW);
        t2.updateCategory(TaskCategory.PERSONAL);
        taskRepository.save(t2);

        Task t3 = createTaskUseCase.execute("Corrigir bug crítico", "Login quebrando em produção", loggedUser, null, null);
        t3.updatePriority(TaskPriority.HIGH);
        t3.updateCategory(TaskCategory.WORK);
        t3.startTask();
        t3.completeTask();
        taskRepository.save(t3);

        Task t4 = createTaskUseCase.execute("Estudar Java", "Testar conexão MySQL", loggedUser, null, null);
        t4.updatePriority(TaskPriority.MEDIUM);
        t4.updateCategory(TaskCategory.STUDY);
        taskRepository.save(t4);

        Task t5 = createTaskUseCase.execute("Preparar apresentação", "Reunião com o time", loggedUser, null, null);
        t5.updatePriority(TaskPriority.HIGH);
        t5.updateCategory(TaskCategory.WORK);
        taskRepository.save(t5);

        System.out.println("\n5 tasks criadas.\n");

        ListTasksUseCase listTasksUseCase = new ListTasksUseCase(taskRepository);

        System.out.println("== Todas as tasks (sem filtro, ordenadas por prioridade) ==");
        printTasks(listTasksUseCase.execute(loggedUser.getId(), null));

        System.out.println("\n== Só prioridade HIGH ==");
        printTasks(listTasksUseCase.execute(loggedUser.getId(),
                ListTasksUseCase.TaskFilter.byPriority(TaskPriority.HIGH)));

        System.out.println("\n== Só categoria WORK ==");
        printTasks(listTasksUseCase.execute(loggedUser.getId(),
                ListTasksUseCase.TaskFilter.byCategory(TaskCategory.WORK)));

        System.out.println("\n== Combinando: HIGH + WORK + IN_PROGRESS ==");
        printTasks(listTasksUseCase.execute(loggedUser.getId(),
                new ListTasksUseCase.TaskFilter(TaskStatus.IN_PROGRESS, TaskPriority.HIGH, TaskCategory.WORK)));

        System.out.println("\n== Combinando: HIGH + WORK + COMPLETED ==");
        printTasks(listTasksUseCase.execute(loggedUser.getId(),
                new ListTasksUseCase.TaskFilter(TaskStatus.COMPLETED, TaskPriority.HIGH, TaskCategory.WORK)));

        // connection.close();
        ApiServer apiServer = new ApiServer(createTaskUseCase, listTasksUseCase, userRepository);
        apiServer.start(8080);
    }

    private static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("(nenhuma task encontrada)");
            return;
        }
        for (Task task : tasks) {
            System.out.println("- " + task);
        }
    }
}