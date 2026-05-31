package com.shopflow.productservice.products;

import com.shopflow.productservice.categories.Category;
import com.shopflow.productservice.categories.CategoryRepository;
import com.shopflow.productservice.common.exception.DuplicateResourceException;
import com.shopflow.productservice.common.exception.ResourceNotFoundException;
import com.shopflow.productservice.products.dto.CreateProductDto;
import com.shopflow.productservice.products.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public List<ProductDto> addProduct(List<CreateProductDto> CreateProductPayload) {
       List<Product> products =  CreateProductPayload.stream().map(dto->{
            boolean flag = this.productRepository.existsByName(dto.getName());
            if(flag){
                throw new DuplicateResourceException(
                        "Product with name " + dto.getName() + " already exists"
                );
            }
            Category category = this.categoryRepository.findById(dto.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Category", dto.getCategoryId()));

            Product product = Product.builder()
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .price(dto.getPrice())
                    .stockQuantity(dto.getStockQuantity())
                    .isActive(true)
                    .category(category)
                    .build();
            return product;

        }).toList();
        List<Product> savedProducts = productRepository.saveAll(products);

        return savedProducts.stream()
                .map(this::convertToDto)
                .toList();
    }
    // ─── Convert Entity to DTO ───────────────────
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Set category summary
        if (product.getCategory() != null) {
            ProductDto.CategorySummary categorySummary =
                    new ProductDto.CategorySummary();
            categorySummary.setId(product.getCategory().getId());
            categorySummary.setName(product.getCategory().getName());
            dto.setCategory(categorySummary);
        }

        return dto;
    }
}
