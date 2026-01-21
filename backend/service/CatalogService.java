package backend.service;

import backend.entity.Product;
import backend.repository.ProductRepository;
import java.util.List;

public class CatalogService {
    private final ProductRepository productRepository = new ProductRepository();

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
