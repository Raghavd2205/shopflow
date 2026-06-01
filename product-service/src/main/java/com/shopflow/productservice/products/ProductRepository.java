package com.shopflow.productservice.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    public boolean existsByName(String Name);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.category " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :value, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :value, '%')) " +
            "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :value, '%')) " +
            "OR LOWER(p.category.description) LIKE LOWER(CONCAT('%', :value, '%'))")
    List<Product> searchProduct(@Param("value") String value);
}
