package backend.repository;

import backend.config.DatabaseConfig;
import backend.dto.FullOrderDTO;
import backend.entity.Order;
import backend.enums.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    // --- СОХРАНЕНИЕ ЗАКАЗА ---
    public Long save(Order order) {
        String sql = """
        INSERT INTO orders (user_id, total_amount, status, address)
        VALUES (?, ?, ?::order_status, ?)
        RETURNING id
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, order.getUserId());
            stmt.setBigDecimal(2, order.getTotalAmount());
            stmt.setString(3, order.getStatus().name());
            stmt.setString(4, order.getAddress());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- ПОЛУЧИТЬ ОДИН ЗАКАЗ ---
    public FullOrderDTO getFullOrderDescription(Long orderId) {
        FullOrderDTO dto = null;
        String sql = """
            SELECT o.id, o.total_amount, o.status, u.email,
                   p.name as prod_name, c.name as cat_name, 
                   oi.quantity, oi.unit_price
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
                        rs.getInt("quantity"), rs.getBigDecimal("unit_price")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dto;
    }

    // --- 🔥 НОВЫЙ МЕТОД: ИСТОРИЯ ЗАКАЗОВ ЮЗЕРА ---
    public List<FullOrderDTO> findOrdersByUser(Long userId) {
        List<FullOrderDTO> orders = new ArrayList<>();
        String sql = """
            SELECT o.id, o.total_amount, o.status, u.email,
                   p.name as prod_name, c.name as cat_name, 
                   oi.quantity, oi.unit_price
            FROM orders o
            JOIN users u ON o.user_id = u.id
            JOIN order_items oi ON o.id = oi.order_id
            JOIN products p ON oi.product_id = p.id
            JOIN categories c ON p.category_id = c.id
            WHERE o.user_id = ?
            ORDER BY o.id DESC
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            FullOrderDTO currentOrder = null;
            Long lastOrderId = null;

            while (rs.next()) {
                Long orderId = rs.getLong("id");

                // Если сменился ID заказа, значит началась новая группа строк
                if (!orderId.equals(lastOrderId)) {
                    // Сохраняем предыдущий заказ в список
                    if (currentOrder != null) orders.add(currentOrder);

                    // Создаем новый
                    currentOrder = new FullOrderDTO();
                    currentOrder.orderId = orderId;
                    currentOrder.totalAmount = rs.getBigDecimal("total_amount");
                    currentOrder.status = OrderStatus.valueOf(rs.getString("status"));
                    currentOrder.buyerEmail = rs.getString("email");

                    lastOrderId = orderId;
                }

                // Добавляем товар в текущий заказ
                currentOrder.items.add(new FullOrderDTO.OrderItemInfo(
                        rs.getString("prod_name"), rs.getString("cat_name"),
                        rs.getInt("quantity"), rs.getBigDecimal("unit_price")
                ));
            }

            // Не забываем добавить самый последний заказ после выхода из цикла
            if (currentOrder != null) orders.add(currentOrder);

        } catch (SQLException e) { e.printStackTrace(); }

        return orders;
    }
}
