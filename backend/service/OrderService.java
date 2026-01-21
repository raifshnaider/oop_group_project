package backend.service;

import backend.config.SessionContext;
import backend.dto.FullOrderDTO;
import backend.entity.Order;
import backend.entity.Product;
import backend.enums.OrderStatus;
import backend.repository.*;
import java.math.BigDecimal;
import java.util.Map;

public class OrderService {
    private final ProductRepository productRepository = new ProductRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final OrderItemRepository orderItemRepository = new OrderItemRepository();

    public FullOrderDTO placeOrder(Map<Long, Integer> cart) {
        Long userId = SessionContext.getInstance().getCurrentUser().getId();
        BigDecimal total = BigDecimal.ZERO;

        // 1. Считаем сумму
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product p = productRepository.findById(entry.getKey()).orElseThrow();
            if (p.getStock() < entry.getValue()) throw new RuntimeException("Not enough stock: " + p.getName());
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        // 2. Создаем заказ
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CREATED);
        Long orderId = orderRepository.save(order);

        // 3. Сохраняем позиции
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product p = productRepository.findById(entry.getKey()).get();
            orderItemRepository.saveRaw(orderId, p.getId(), entry.getValue(), p.getPrice());
            productRepository.updateStock(p.getId(), p.getStock() - entry.getValue());
        }

        return orderRepository.getFullOrderDescription(orderId);
    }
}
