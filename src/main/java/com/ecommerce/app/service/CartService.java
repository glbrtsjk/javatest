package com.ecommerce.app.service;

import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.CartItem;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.repository.CartItemRepository;
import com.ecommerce.app.repository.CartRepository;
import com.ecommerce.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByUser(User user) {
        Optional<Cart> cart = cartRepository.findByUser(user);
        if (cart.isPresent()) {
            return cart.get();
        } else {
            // Create a new cart if none exists
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        }
    }

    public String addToCart(User user, Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            return "Product not found";
        }

        Product product = productOpt.get();
        if (product.getStockQuantity() < quantity) {
            return "Insufficient stock";
        }

        Cart cart = getCartByUser(user);
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            if (product.getStockQuantity() < newQuantity) {
                return "Insufficient stock";
            }
            
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(cart, product, quantity);
            cartItemRepository.save(cartItem);
        }

        return "Item added to cart successfully";
    }

    public String updateCartItem(User user, Long productId, Integer quantity) {
        Cart cart = getCartByUser(user);
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (!productOpt.isPresent()) {
            return "Product not found";
        }

        Product product = productOpt.get();
        Optional<CartItem> cartItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        if (!cartItemOpt.isPresent()) {
            return "Item not found in cart";
        }

        CartItem cartItem = cartItemOpt.get();
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return "Item removed from cart";
        }

        if (product.getStockQuantity() < quantity) {
            return "Insufficient stock";
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return "Cart updated successfully";
    }

    public String removeFromCart(User user, Long productId) {
        Cart cart = getCartByUser(user);
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (!productOpt.isPresent()) {
            return "Product not found";
        }

        Product product = productOpt.get();
        Optional<CartItem> cartItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        if (!cartItemOpt.isPresent()) {
            return "Item not found in cart";
        }

        cartItemRepository.delete(cartItemOpt.get());
        return "Item removed from cart successfully";
    }

    public void clearCart(User user) {
        Cart cart = getCartByUser(user);
        for (CartItem item : cart.getItems()) {
            cartItemRepository.delete(item);
        }
    }
}