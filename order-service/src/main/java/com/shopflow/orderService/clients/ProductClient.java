package com.shopflow.orderService.clients;

import com.shopflow.orderService.clients.dto.ProductDto;
import com.shopflow.orderService.clients.dto.UpdateStockDto;
import com.shopflow.orderService.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "product-service",
        url = "${product.service.url}"
)
public interface ProductClient {

    @GetMapping("/api/v1/product/{id}")
    ApiResponse<ProductDto> getProductById(@PathVariable Long id);

    @PatchMapping("/api/v1/product/{id}/stock")
    ApiResponse<ProductDto> updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockDto dto
    );
}