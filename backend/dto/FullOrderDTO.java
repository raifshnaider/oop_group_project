package backend.dto;

import backend.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FullOrderDTO {
    public Long orderId;
    public String buyerEmail;
    public BigDecimal totalAmount;
    public OrderStatus status;
    public List<OrderItemInfo> items = new ArrayList<>();

    public static class OrderItemInfo {
        public String productName;
        public String categoryName;
        public int quantity;
        public BigDecimal price;

        public OrderItemInfo(String pName, String cName, int qty, BigDecimal price) {
            this.productName = pName;
            this.categoryName = cName;
            this.quantity = qty;
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("- %s (%s): %d pcs * $%.2f", productName, categoryName, quantity, price);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(orderId).append("\n");
        sb.append("Buyer: ").append(buyerEmail).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Total: $").append(totalAmount).append("\n");
        sb.append("Items:\n");
        for (OrderItemInfo item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
