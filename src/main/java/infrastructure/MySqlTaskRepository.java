package infrastructure;

import domain.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlTaskRepository implements TaskRepository {

    @Override
    public Task save(Task task) {
        String sql = """
                INSERT INTO tasks (id, title, description, status, priority, category, owner_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    title = VALUES(title),
                    description = VALUES(description),
                    status = VALUES(status),
                    priority = VALUES(priority),
                    category = VALUES(category),
                    owner_id = VALUES(owner_id)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getStatus().name());
            ps.setString(5, task.getPriority().name());
            ps.setString(6, task.getCategory().name());
            ps.setString(7, task.getOwnerId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Erro ao inserir valores.", e);
        }
        return task;
    }

    @Override
    public List<Task> findAllByOwner(String ownerId) {
        String sql = "SELECT * FROM tasks WHERE owner_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)){
            List<Task> allTasks = new ArrayList<>();
            ps.setString(1, ownerId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String textId = rs.getString("id");
                    String textOwnerId = rs.getString("owner_id");
                    TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
                    TaskPriority priority = TaskPriority.valueOf(rs.getString("priority"));
                    TaskCategory category = TaskCategory.valueOf(rs.getString("category"));
                    Task task = Task.rebuiltedTask(title, description, status, textId, priority, category, textOwnerId);
                    allTasks.add(task);
                }
                return allTasks;
            }

        } catch (SQLException e) {
            throw new RepositoryException("Erro ao listar tarefa", e);
        }
    }

    @Override
    public Optional<Task> findById(String id) {
        String sql = "SELECT * FROM tasks WHERE Id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String textId = rs.getString("id");
                    String ownerId = rs.getString("owner_id");
                    TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
                    TaskPriority priority = TaskPriority.valueOf(rs.getString("priority"));
                    TaskCategory category = TaskCategory.valueOf(rs.getString("category"));
                    Task task = Task.rebuiltedTask(title, description, status, textId, priority, category, ownerId);
                    return Optional.of(task);
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Erro ao buscar tarefa por id", e);
        }
    }

    @Override
    public void removeTask(String id){
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Erro ao remover tarefa", e);
        }
    }

    private final Connection connection;

    public MySqlTaskRepository(Connection connection){
        this.connection = connection;
    }
}