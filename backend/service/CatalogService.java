package backend.service;

import backend.entity.Product;
import backend.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CatalogService {
    private final ProductRepository productRepository = new ProductRepository();
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    public void addNewProduct(String name, BigDecimal price, int stock, Long categoryId) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive!");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative!");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty!");
        }

        Product p = new Product(name, price, stock, categoryId);
        productRepository.save(p);
    }
    public void changePrice(Long productId, BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive!");
        }
        productRepository.updatePrice(productId, newPrice);
    }

    public void addStock(Long productId, int quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity must be positive!");
        }

        productRepository.findById(productId).ifPresentOrElse(p -> {
            int newStock = p.getStock() + quantityToAdd;
            productRepository.updateStock(productId, newStock);
        }, () -> {
            throw new IllegalArgumentException("Product not found!");
        });
    }
    public void deleteProduct(Long productId) {
        if (productRepository.findById(productId).isEmpty()) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found!");
        }
        productRepository.delete(productId);
    }
}
