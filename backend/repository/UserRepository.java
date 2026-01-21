package backend.repository;

import backend.config.DatabaseConfig;
import backend.entity.User;
import backend.enums.Role;

import java.sql.*;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByEmail(String email) {
        String sql = """
            SELECT id, email, password_hash, role
            FROM users
            WHERE email = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                User u = new User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRole(Role.valueOf(rs.getString("role")));

                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
