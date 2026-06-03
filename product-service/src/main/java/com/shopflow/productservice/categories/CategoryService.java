package com.shopflow.productservice.categories;

import com.shopflow.productservice.categories.dto.CategoryDto;
import com.shopflow.productservice.categories.dto.CreateCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> addCategories(List<CreateCategoryDto> createCategoryPayload);
    String deleteCategory(long categoryId);
}
