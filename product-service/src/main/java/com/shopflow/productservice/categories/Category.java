package com.shopflow.productservice.categories;

import com.shopflow.productservice.products.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // One category → Many products
    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY
    )
    private List<Product> products;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}