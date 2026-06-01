package com.shopflow.productservice.products;

import com.shopflow.productservice.categories.Category;
import com.shopflow.productservice.categories.CategoryRepository;
import com.shopflow.productservice.common.exception.DuplicateResourceException;
import com.shopflow.productservice.common.exception.ResourceNotFoundException;
import com.shopflow.productservice.products.dto.CreateProductDto;
import com.shopflow.productservice.products.dto.ProductDto;
import com.shopflow.productservice.products.dto.updateProductDto;
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

    @Override
    public List<ProductDto> listAllProduct() {
        List<Product> allProducts= this.productRepository.findAll();
        List<ProductDto> ProductDto = allProducts.stream().map(this::convertToDto).toList();
        return ProductDto;
    }

    @Override
    public ProductDto listProductById(Long id) {
        ProductDto product = convertToDto(this.productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product not found with id",id)));
        return product;
    }

    @Override
    public ProductDto updateProduct(updateProductDto CreateProductPayload) {
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
