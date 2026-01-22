package console;

import backend.config.SessionContext;
import backend.dto.FullOrderDTO;
import backend.service.OrderService;
import java.util.List;

public class OrderMenu {
    private final OrderService orderService = new OrderService();

    public void showMyOrders() {
        Long userId = SessionContext.getInstance().getCurrentUser().getId();

        List<FullOrderDTO> orders = orderService.getOrdersByUser(userId);

        if (orders.isEmpty()) {
            System.out.println("\n📦 У вас пока нет заказов.");
            return;
        }

        System.out.println("\n=== ИСТОРИЯ ЗАКАЗОВ ===");

        for (FullOrderDTO order : orders) {
            System.out.println("\n---------------------------------");
            System.out.println("Заказ #" + order.orderId);
            System.out.println("Статус: " + order.status);
            System.out.println("Дата:   " + (order.orderId)); // Тут можно дату, если добавишь поле в DTO
            System.out.println("Сумма:  $" + order.totalAmount);
            System.out.println("Товары:");

            for (FullOrderDTO.OrderItemInfo item : order.items) {
                System.out.printf(" - %s x%d ($%.2f)\n", item.productName, item.quantity, item.price);
            }
        }
        System.out.println("---------------------------------");
    }
}
