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

    // Новый метод: с адресом
    public FullOrderDTO placeOrder(Map<Long, Integer> cart, String address) {
        if (address == null || address.trim().length() < 5) {
            throw new IllegalArgumentException("Address is required (min 5 chars)");
        }

        Long userId = SessionContext.getInstance().getCurrentUser().getId();
        BigDecimal total = BigDecimal.ZERO;

        // 1. Считаем сумму + проверяем остаток
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product p = productRepository.findById(entry.getKey()).orElseThrow();
            if (p.getStock() < entry.getValue()) {
                throw new RuntimeException("Not enough stock: " + p.getName());
            }
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        // 2. Создаем заказ
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CREATED);
        order.setAddress(address.trim()); // ✅ ВАЖНО
        Long orderId = orderRepository.save(order);

        if (orderId == null) {
            throw new RuntimeException("Order was not saved (orderId is null)");
        }

        // 3. Сохраняем позиции + обновляем склад
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product p = productRepository.findById(entry.getKey()).orElseThrow();
            orderItemRepository.saveRaw(orderId, p.getId(), entry.getValue(), p.getPrice());
            productRepository.updateStock(p.getId(), p.getStock() - entry.getValue());
        }

        return orderRepository.getFullOrderDescription(orderId);
    }

    // Старый метод оставим для совместимости (если где-то ещё вызывается)
    public FullOrderDTO placeOrder(Map<Long, Integer> cart) {
        return placeOrder(cart, "Test address");
    }
}
