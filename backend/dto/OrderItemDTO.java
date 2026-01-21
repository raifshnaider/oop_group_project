package backend.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
    public String productName;
    public int quantity;
    public BigDecimal price;

    public OrderItemDTO(String productName, int quantity, BigDecimal price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
}
