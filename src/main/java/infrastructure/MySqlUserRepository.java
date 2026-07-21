package infrastructure;

import domain.User;
import domain.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MySqlUserRepository implements UserRepository {

    private final Connection connection;

    public MySqlUserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users (id, username, password_hash)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    username = VALUES(username),
                    password_hash = VALUES(password_hash)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPasswordHash());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao salvar usuário.", e);
        }
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String passwordHash = rs.getString("password_hash");
                    User user = User.rebuiltedUser(id, username, passwordHash);
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao buscar usuário por username", e);
        }
    }

    @Override
    public void deleteAccount(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao remover usuário", e);
        }
    }
}