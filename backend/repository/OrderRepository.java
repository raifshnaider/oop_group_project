package backend.repository;

import backend.config.DatabaseConfig;
import backend.dto.FullOrderDTO;
import backend.entity.Order;
import backend.enums.OrderStatus;
import java.sql.*;

public class OrderRepository {
    public Long save(Order order) {
        String sql = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, order.getUserId());
            stmt.setBigDecimal(2, order.getTotalAmount());
            stmt.setString(3, order.getStatus().name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public FullOrderDTO getFullOrderDescription(Long orderId) {
        FullOrderDTO dto = null;
        String sql = """
            SELECT o.id, o.total_amount, o.status, u.email,
                   p.name as prod_name, c.name as cat_name, 
                   oi.quantity, oi.price_at_purchase
            FROM orders o
            JOIN users u ON o.user_id = u.id
            JOIN order_items oi ON o.id = oi.order_id
            JOIN products p ON oi.product_id = p.id
            JOIN categories c ON p.category_id = c.id
            WHERE o.id = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (dto == null) {
                    dto = new FullOrderDTO();
                    dto.orderId = rs.getLong("id");
                    dto.totalAmount = rs.getBigDecimal("total_amount");
                    dto.status = OrderStatus.valueOf(rs.getString("status"));
                    dto.buyerEmail = rs.getString("email");
                }
                dto.items.add(new FullOrderDTO.OrderItemInfo(
                        rs.getString("prod_name"), rs.getString("cat_name"),
                        rs.getInt("quantity"), rs.getBigDecimal("price_at_purchase")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dto;
    }
}
