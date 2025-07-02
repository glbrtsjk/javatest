package com.ecommerce.app.controller;

import com.ecommerce.app.dto.AddToCartRequest;
import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request, 
                                      Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String result = cartService.addToCart(user, request.getProductId(), request.getQuantity());
        
        if (result.contains("successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(@Valid @RequestBody AddToCartRequest request, 
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String result = cartService.updateCartItem(user, request.getProductId(), request.getQuantity());
        
        if (result.contains("successfully") || result.contains("removed")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, 
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String result = cartService.removeFromCart(user, productId);
        
        if (result.contains("successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        cartService.clearCart(user);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}