package com.shopflow.productservice.categories;

import com.shopflow.productservice.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category,Long> {
    public boolean existsByName(String name);
}
