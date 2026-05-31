package com.shopflow.productservice.products;

import com.shopflow.productservice.products.dto.CreateProductDto;
import com.shopflow.productservice.products.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> addProduct(List<CreateProductDto> CreateProductPayload);
}
