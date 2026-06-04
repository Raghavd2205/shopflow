package com.shopflow.productservice.products;

import com.shopflow.productservice.products.dto.CreateProductDto;
import com.shopflow.productservice.products.dto.ProductDto;
import com.shopflow.productservice.products.dto.UpdateProductDto;
import com.shopflow.productservice.products.dto.UpdateStockDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> addProduct(List<CreateProductDto> CreateProductPayload);
    List<ProductDto> listAllProduct();
    ProductDto listProductById(Long id);
    ProductDto updateProduct(UpdateProductDto CreateProductPayload);
    List<ProductDto> productSearch(String value);
    ProductDto updateStock(Long id, UpdateStockDto payload);
}
