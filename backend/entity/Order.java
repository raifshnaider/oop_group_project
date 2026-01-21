package backend.entity;

import backend.enums.OrderStatus;
import java.math.BigDecimal;

public class Order {

    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String address;   // ✅ ОБЯЗАТЕЛЬНО

    public Order() {}

    // getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public String getAddress() { return address; } // ✅

    // setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setAddress(String address) { this.address = address; } // ✅
}
