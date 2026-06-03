package com.shopflow.productservice.categories;

import com.shopflow.productservice.categories.dto.CategoryDto;
import com.shopflow.productservice.categories.dto.CreateCategoryDto;
import com.shopflow.productservice.common.exception.DuplicateResourceException;
import com.shopflow.productservice.common.exception.ResourceNotFoundException;
import com.shopflow.productservice.products.Product;
import com.shopflow.productservice.products.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public List<CategoryDto> addCategories(List<CreateCategoryDto> createCategoryPayload) {
        List<Category> categories = createCategoryPayload.stream().map(payload->{
            if(this.categoryRepository.existsByName(payload.getName())){
                throw new DuplicateResourceException(
                        "Category with name " + payload.getName() + " already exists"
                );
            }
            Category category  = Category.builder()
                    .name(payload.getName())
                    .description(payload.getDescription())
                    .build();
            return category;
        }).toList();
        List<Category> savedCategories = this.categoryRepository.saveAll(categories);
        return savedCategories.stream().map(this::toCategoryDto).toList();
    }

    @Override
    public String deleteCategory(long categoryId) {
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category with Id"+categoryId+" id not present"));
        List<Product> products = this.productRepository.findActiveProductByCategoryId(categoryId);
        if(!products.isEmpty()){
            throw new RuntimeException("Unable to delete category with active products");
        }
        this.categoryRepository.delete(category);
        if(this.categoryRepository.existsById(categoryId)){
            throw new BadRequestException("Something went wrong");
        }
        return "Category with Id"+categoryId+" deleted successfully";
    }

    public CategoryDto toCategoryDto(Category category){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setCreatedAt(category.getCreatedAt());
//        categoryDto.setTotalProducts(category.getProducts().size());
        return categoryDto;
    }
}
