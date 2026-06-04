package com.shopflow.productservice.products;

import com.shopflow.productservice.categories.Category;
import com.shopflow.productservice.categories.CategoryRepository;
import com.shopflow.productservice.common.exception.DuplicateResourceException;
import com.shopflow.productservice.common.exception.ResourceNotFoundException;
import com.shopflow.productservice.config.CacheConfig;
import com.shopflow.productservice.products.dto.CreateProductDto;
import com.shopflow.productservice.products.dto.ProductDto;
import com.shopflow.productservice.products.dto.UpdateProductDto;
import com.shopflow.productservice.products.dto.UpdateStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CATEGORIES_CACHE, allEntries = true)
    })
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

    @Override
    // @Cacheable — cache result in Redis
    // value = cache name, key = cache key
    // If cache hit → return from Redis, skip method body
    // If cache miss → execute method, store result in Redis
    @Cacheable(
            value = CacheConfig.PRODUCTS_CACHE,
            key = "'all'"
    )
    public List<ProductDto> listAllProduct() {
        List<Product> allProducts= this.productRepository.findAll();
        List<ProductDto> ProductDto = allProducts.stream().map(this::convertToDto).toList();
        return ProductDto;
    }

    @Override
    // Cache per product id
    // key = "1", "2", "3" etc
    @Cacheable(
            value = CacheConfig.PRODUCT_CACHE,
            key = "#id"
    )
    public ProductDto listProductById(Long id) {
        ProductDto product = convertToDto(this.productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product not found with id",id)));
        System.out.println("product"+product);
        return product;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PRODUCT_CACHE, key="#id"),
            @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CATEGORIES_CACHE, allEntries = true)
    })
    public ProductDto updateProduct(UpdateProductDto CreateProductPayload) {
        Product product = this.productRepository.findById(CreateProductPayload.getId()).orElseThrow(()->new ResourceNotFoundException("Product not found with id",CreateProductPayload.getId()));
        Category category = this.categoryRepository.findById(CreateProductPayload.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Category", CreateProductPayload.getCategoryId()));
        product.setName(CreateProductPayload.getName());
        product.setDescription(CreateProductPayload.getDescription());
        product.setCategory(category);
        product.setStockQuantity(CreateProductPayload.getStockQuantity());
        product.setPrice(CreateProductPayload.getPrice());
        product.setIsActive(true);

        return convertToDto(this.productRepository.save(product));
    }

    @Override
    public List<ProductDto> productSearch(String value) {
        List<Product> product = this.productRepository.searchProduct(value);
        System.out.println("product"+product);
        return product.stream().map(this::convertToDto).toList();
    }



    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PRODUCT_CACHE, key="#id"),
            @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.CATEGORIES_CACHE, allEntries = true)
    })
    public ProductDto updateStock(Long id, UpdateStockDto payload) {
        Product product = this.productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found with id : "+id));
        product.setStockQuantity(payload.getStockQuantity());
        return this.convertToDto(this.productRepository.save(product));
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
