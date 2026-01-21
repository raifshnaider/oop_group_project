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
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                return Optional.of(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public long save(User user) {
        String sql = """
        INSERT INTO users (email, password_hash, role, full_name, phone, is_active)
        VALUES (?, ?, ?::user_role, ?, ?, ?)
        RETURNING id
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());   // кастится в user_role
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getPhone());
            stmt.setBoolean(6, user.isActive());        // ✅ ВОТ ОН, параметр 6

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

}
