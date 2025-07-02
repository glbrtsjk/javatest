package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category = :category AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> findByCategoryAndKeyword(@Param("category") Category category, @Param("keyword") String keyword, Pageable pageable);
    
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
}