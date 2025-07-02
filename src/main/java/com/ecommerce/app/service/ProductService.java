package com.ecommerce.app.service;

import com.ecommerce.app.entity.Category;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.repository.CategoryRepository;
import com.ecommerce.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            return productRepository.findByCategory(category.get(), pageable);
        }
        return Page.empty();
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByKeyword(keyword, pageable);
    }

    public Page<Product> searchProductsByCategory(Long categoryId, String keyword, Pageable pageable) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            return productRepository.findByCategoryAndKeyword(category.get(), keyword, pageable);
        }
        return Page.empty();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByStockQuantityGreaterThan(0);
    }
}