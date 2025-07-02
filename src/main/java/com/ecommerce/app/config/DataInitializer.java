package com.ecommerce.app.config;

import com.ecommerce.app.entity.Category;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.repository.CategoryRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@ecommerce.com", 
                passwordEncoder.encode("admin123"), "Admin", "User");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }

        // Create test user
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = new User("testuser", "test@ecommerce.com", 
                passwordEncoder.encode("test123"), "Test", "User");
            testUser.setAddress("123 Test Street, Test City");
            testUser.setPhone("555-1234");
            userRepository.save(testUser);
        }

        // Create categories
        Category electronics = createCategoryIfNotExists("Electronics", "Electronic devices and gadgets");
        Category clothing = createCategoryIfNotExists("Clothing", "Fashion and apparel");
        Category books = createCategoryIfNotExists("Books", "Books and literature");
        Category home = createCategoryIfNotExists("Home & Garden", "Home improvement and garden supplies");

        // Create products
        createProductIfNotExists("Smartphone", "Latest model smartphone with advanced features", 
            new BigDecimal("699.99"), 50, electronics);
        createProductIfNotExists("Laptop", "High-performance laptop for work and gaming", 
            new BigDecimal("1299.99"), 25, electronics);
        createProductIfNotExists("T-Shirt", "Comfortable cotton t-shirt", 
            new BigDecimal("19.99"), 100, clothing);
        createProductIfNotExists("Jeans", "Classic denim jeans", 
            new BigDecimal("49.99"), 75, clothing);
        createProductIfNotExists("Programming Book", "Learn programming from scratch", 
            new BigDecimal("29.99"), 40, books);
        createProductIfNotExists("Garden Tools Set", "Complete set of garden tools", 
            new BigDecimal("89.99"), 30, home);
    }

    private Category createCategoryIfNotExists(String name, String description) {
        return categoryRepository.findByName(name)
            .orElseGet(() -> categoryRepository.save(new Category(name, description)));
    }

    private void createProductIfNotExists(String name, String description, BigDecimal price, 
                                        Integer stock, Category category) {
        if (productRepository.findAll().stream().noneMatch(p -> p.getName().equals(name))) {
            Product product = new Product(name, description, price, stock, category);
            productRepository.save(product);
        }
    }
}