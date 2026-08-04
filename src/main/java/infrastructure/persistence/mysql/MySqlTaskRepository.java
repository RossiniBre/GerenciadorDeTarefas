package infrastructure.persistence.mysql;

import domain.model.Task;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.model.TaskStatus;
import domain.repositories.TaskRepository;
import infrastructure.config.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlTaskRepository implements TaskRepository {

    @Override
    public Task save(Task task) {
        String sql = """
                INSERT INTO tasks (id, title, description, status, priority, category, owner_id, due_date, reminder_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    title = VALUES(title),
                    description = VALUES(description),
                    status = VALUES(status),
                    priority = VALUES(priority),
                    category = VALUES(category),
                    owner_id = VALUES(owner_id),
                    due_date = VALUES(due_date),
                    reminder_date = VALUES(reminder_date)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getStatus().name());
            ps.setString(5, task.getPriority().name());
            ps.setString(6, task.getCategory().name());
            ps.setString(7, task.getOwnerId());
            setNullableTimestamp(ps, 8, task.getDueDate());
            setNullableTimestamp(ps, 9, task.getReminderDate());
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
                    allTasks.add(mapRow(rs));
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
                    return Optional.of(mapRow(rs));
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

    private Task mapRow(ResultSet rs) throws SQLException {
        String title = rs.getString("title");
        String description = rs.getString("description");
        String textId = rs.getString("id");
        String ownerId = rs.getString("owner_id");
        TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
        TaskPriority priority = TaskPriority.valueOf(rs.getString("priority"));
        TaskCategory category = TaskCategory.valueOf(rs.getString("category"));
        LocalDateTime dueDate = readNullableTimestamp(rs, "due_date");
        LocalDateTime reminderDate = readNullableTimestamp(rs, "reminder_date");
        return Task.rebuiltTask(title, description, status, textId, priority, category, ownerId, dueDate, reminderDate);
    }

    private void setNullableTimestamp(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if (value != null) {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        } else {
            ps.setNull(index, Types.TIMESTAMP);
        }
    }

    private LocalDateTime readNullableTimestamp(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private final Connection connection;

    public MySqlTaskRepository(Connection connection){
        this.connection = connection;
    }
}