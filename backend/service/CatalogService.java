package backend.service;

import backend.entity.Product;
import backend.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CatalogService {
    private final ProductRepository productRepository = new ProductRepository();

    // --- КЛИЕНТСКИЕ МЕТОДЫ ---

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // --- МЕТОДЫ ДЛЯ МЕНЕДЖЕРА ---

    public void addNewProduct(String name, BigDecimal price, int stock, Long categoryId) {
        Product p = new Product(name, price, stock, categoryId);
        productRepository.save(p);
    }

    public void changePrice(Long productId, BigDecimal newPrice) {
        productRepository.updatePrice(productId, newPrice);
    }

    public void addStock(Long productId, int quantityToAdd) {
        // Сначала получаем текущий товар, чтобы узнать старый stock
        productRepository.findById(productId).ifPresent(p -> {
            int newStock = p.getStock() + quantityToAdd;
            productRepository.updateStock(productId, newStock);
        });
    }
}
