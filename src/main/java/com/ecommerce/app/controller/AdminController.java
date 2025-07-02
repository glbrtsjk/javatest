package com.ecommerce.app.controller;

import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.OrderService;
import com.ecommerce.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // User management
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        
        User user = userOpt.get();
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok("User role updated successfully");
    }

    // Product management (already covered in ProductController, but adding admin-specific endpoints)
    @GetMapping("/products/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        List<Product> products = productService.getAllProducts().stream()
                .filter(p -> p.getStockQuantity() <= threshold)
                .toList();
        return ResponseEntity.ok(products);
    }

    // Order management (already covered in OrderController, but adding admin-specific endpoints)
    @GetMapping("/orders/pending")
    public ResponseEntity<List<Order>> getPendingOrders() {
        List<Order> orders = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalProducts = productService.getAllProducts().size();
        long totalOrders = orderService.getAllOrders().size();
        long pendingOrders = orderService.getOrdersByStatus(Order.OrderStatus.PENDING).size();
        
        return ResponseEntity.ok(new DashboardStats(totalUsers, totalProducts, totalOrders, pendingOrders));
    }

    public static class DashboardStats {
        private long totalUsers;
        private long totalProducts;
        private long totalOrders;
        private long pendingOrders;

        public DashboardStats(long totalUsers, long totalProducts, long totalOrders, long pendingOrders) {
            this.totalUsers = totalUsers;
            this.totalProducts = totalProducts;
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getTotalProducts() { return totalProducts; }
        public long getTotalOrders() { return totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
    }
}