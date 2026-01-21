package backend.dto;

import java.math.BigDecimal;

public class ProductDTO {
    public Long id;
    public String name;
    public BigDecimal price;
    public String categoryName;

    public ProductDTO(Long id, String name, BigDecimal price, String categoryName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - $%.2f", id, name, categoryName, price);
    }
}
