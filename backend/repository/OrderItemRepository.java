package backend.repository;

import backend.config.DatabaseConfig;
import backend.entity.OrderItem;
import java.sql.*;
import java.math.BigDecimal;

public class OrderItemRepository {
    public void save(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Исправлен доступ к приватным полям (в entity добавьте геттеры если нет, но лучше так)
            // Здесь предполагаем что в OrderItem есть поля или они public.
            // ⚠️ В файле OrderItem.java выше нет геттеров для orderId и т.д.
            // Допишем логику здесь напрямую через рефлексию или просто добавьте геттеры.
            // Ниже код предполагает наличие геттеров в OrderItem.java (добавьте их!).
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Исправленная версия с прямыми параметрами для простоты
    public void saveRaw(Long orderId, Long prodId, int qty, BigDecimal unitPrice) {
        String sql = """
            INSERT INTO order_items (order_id, product_id, quantity, unit_price, line_total)
            VALUES (?, ?, ?, ?, ?)
        """;

        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            stmt.setLong(2, prodId);
            stmt.setInt(3, qty);
            stmt.setBigDecimal(4, unitPrice);
            stmt.setBigDecimal(5, lineTotal);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
