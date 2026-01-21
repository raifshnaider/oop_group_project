package backend.repository;

import backend.config.DatabaseConfig;
import backend.entity.User;
import backend.enums.Role;
import java.sql.*;
import java.util.Optional;

public class UserRepository {
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password_hash"));
                u.setRole(Role.valueOf(rs.getString("role")));
                return Optional.of(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }
}
