package ru.mtuci.rbpo_practice.services;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_practice.models.Product;
import ru.mtuci.rbpo_practice.repositories.ProductRepository;

import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Long createProduct(String name, Boolean isBlocked){
        Product product = new Product();
        product.setName(name);
        product.setBlocked(isBlocked);
        productRepository.save(product);
        return productRepository.findTopByOrderByIdDesc().get().getId();
    }

    public String updateProduct(Long id, String name, Boolean isBlocked) {
        Optional<Product> product = getProductById(id);
        if (product.isEmpty()) {
            return "Product not found.";
        }

        Product productObj = product.get();
        productObj.setBlocked(isBlocked);
        productObj.setName(name);
        productRepository.save(productObj);
        return "OK";
    }
}
