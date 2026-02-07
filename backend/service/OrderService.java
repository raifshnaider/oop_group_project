package backend.service;

import backend.config.SessionContext;
import backend.dto.FullOrderDTO;
import backend.entity.Order;
import backend.entity.Product;
import backend.enums.OrderStatus;
import backend.repository.*;
import java.util.function.Function;
import java.util.function.Supplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final ProductRepository productRepository = new ProductRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final OrderItemRepository orderItemRepository = new OrderItemRepository();

    public FullOrderDTO placeOrder(Map<Long, Integer> cart, String address) {
        if (address == null || address.trim().length() < 5) {
            throw new IllegalArgumentException("Address is required (min 5 chars)");
        }

        Long userId = SessionContext.getInstance().getCurrentUser().getId();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product p = productRepository.findById(entry.getKey()).orElseThrow();
            if (p.getStock() < entry.getValue()) {
                throw new RuntimeException("Not enough stock: " + p.getName());
            }
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CREATED);
        order.setAddress(address.trim());
        Long orderId = orderRepository.save(order);

        if (orderId == null) {
            throw new RuntimeException("Order was not saved (orderId is null)");
        }

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product p = productRepository.findById(entry.getKey()).orElseThrow();
            orderItemRepository.saveRaw(orderId, p.getId(), entry.getValue(), p.getPrice());
            productRepository.updateStock(p.getId(), p.getStock() - entry.getValue());
        }

        return orderRepository.getFullOrderDescription(orderId);
    }

    public FullOrderDTO placeOrder(Map<Long, Integer> cart) {
        Supplier<FullOrderDTO> supplier =
                () -> placeOrder(cart, "Test address");
        return supplier.get();
    }


    public List<FullOrderDTO> getOrdersByUser(Long userId) {
        Function<Long, List<FullOrderDTO>> loader =
                id -> orderRepository.findOrdersByUser(id);

        return loader.apply(userId);
    }


}
