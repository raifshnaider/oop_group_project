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
}
