package com.ecommerce.app.service;

import com.ecommerce.app.entity.*;
import com.ecommerce.app.repository.OrderItemRepository;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    public String createOrder(User user, String shippingAddress) {
        Cart cart = cartService.getCartByUser(user);
        
        if (cart.getItems().isEmpty()) {
            return "Cart is empty";
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Validate stock and calculate total
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                return "Insufficient stock for product: " + product.getName();
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }

        // Create order
        Order order = new Order(user, totalAmount, shippingAddress);
        order = orderRepository.save(order);

        // Create order items and update stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), product.getPrice());
            orderItemRepository.save(orderItem);
            
            // Update stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Clear cart
        cartService.clearCart(user);

        return "Order created successfully with ID: " + order.getId();
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public Page<Order> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public String updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return "Order not found";
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        orderRepository.save(order);
        return "Order status updated successfully";
    }
}