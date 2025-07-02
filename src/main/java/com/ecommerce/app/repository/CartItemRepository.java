package com.ecommerce.app.repository;

import com.ecommerce.app.entity.CartItem;
import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}