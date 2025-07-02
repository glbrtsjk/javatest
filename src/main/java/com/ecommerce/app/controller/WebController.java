package com.ecommerce.app.controller;

import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.service.ProductService;
import com.ecommerce.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        Pageable pageable = PageRequest.of(0, 8);
        Page<Product> products = productService.getAllProducts(pageable);
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("products", products.getContent());
        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/products")
    public String products(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "12") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Long categoryId) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (categoryId != null) {
                productsPage = productService.searchProductsByCategory(categoryId, keyword, pageable);
            } else {
                productsPage = productService.searchProducts(keyword, pageable);
            }
        } else if (categoryId != null) {
            productsPage = productService.getProductsByCategory(categoryId, pageable);
        } else {
            productsPage = productService.getAllProducts(pageable);
        }
        
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        
        return "products";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}