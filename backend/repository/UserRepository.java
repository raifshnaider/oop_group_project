package backend.repository;

import backend.config.DatabaseConfig;
import backend.entity.User;
import backend.enums.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    // --- СТАРЫЙ МЕТОД (ПОИСК ПО EMAIL) ---
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // --- СТАРЫЙ МЕТОД (СОХРАНЕНИЕ) ---
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
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getPhone());
            stmt.setBoolean(6, user.isActive());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 🔥 НОВЫЙ МЕТОД ДЛЯ АДМИНА (ПОЛУЧИТЬ ВСЕХ)
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    // 🔥 НОВЫЙ МЕТОД ДЛЯ АДМИНА (СМЕНА РОЛИ)
    public void updateRole(Long userId, Role newRole) {
        // Каст ::user_role нужен, если у тебя ENUM тип в базе PostgreSQL
        String sql = "UPDATE users SET role = ?::user_role WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole.name());
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Вспомогательный метод, чтобы не дублировать код маппинга
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        // Аккуратно с Enum: если в базе роль 'admin', а в Java 'ADMIN'
        u.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setActive(rs.getBoolean("is_active"));
        return u;
    }
}
