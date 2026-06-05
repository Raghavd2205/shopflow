package com.shopflow.orderservice.clients;

import com.shopflow.orderService.clients.dto.ProductDto;
import com.shopflow.orderService.clients.dto.UpdateStockDto;
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
    ProductDto getProductById(@PathVariable Long id);

    @PatchMapping("/api/v1/product/{id}/stock")
    ProductDto updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockDto dto
    );
}