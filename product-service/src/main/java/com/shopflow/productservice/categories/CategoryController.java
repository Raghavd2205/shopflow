package com.shopflow.productservice.categories;

import com.shopflow.productservice.categories.dto.CategoryDto;
import com.shopflow.productservice.categories.dto.CreateCategoryDto;
import com.shopflow.productservice.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> addCategories(@RequestBody List<CreateCategoryDto> categoryPayload){
        List<CategoryDto> categories  = this.categoryService.addCategories(categoryPayload);
        return ResponseEntity.ok(ApiResponse.created("Categories add succesfully",categories));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable long id){
        return ResponseEntity.ok(ApiResponse.success(this.categoryService.deleteCategory(id)));
    }
}
