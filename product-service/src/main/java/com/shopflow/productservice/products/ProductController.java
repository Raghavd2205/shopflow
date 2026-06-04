package com.shopflow.productservice.products;

import com.shopflow.productservice.common.response.ApiResponse;
import com.shopflow.productservice.products.dto.CreateProductDto;
import com.shopflow.productservice.products.dto.ProductDto;
import com.shopflow.productservice.products.dto.UpdateProductDto;
import com.shopflow.productservice.products.dto.UpdateStockDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> addProducts(@RequestBody List<CreateProductDto> CreateProductPayload){
        ResponseEntity<ApiResponse<List<ProductDto>>> res1 = ResponseEntity.ok(
                ApiResponse.created("Product Added Successfully",this.productService.addProduct(CreateProductPayload)));
        System.out.println("res1 "+res1);
        return res1;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> listAllProducts(){
        ResponseEntity<ApiResponse<List<ProductDto>>> res2 = ResponseEntity.ok(
                ApiResponse.success("All Products listed",this.productService.listAllProduct()));
        System.out.println("res2 "+res2);
        return res2;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> listProductById(@PathVariable Long id){
        ResponseEntity<ApiResponse<ProductDto>> res2 = ResponseEntity.ok(
                ApiResponse.success("Product Added Successfully",this.productService.listProductById(id)));
        System.out.println("res2 "+res2);
        return res2;
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDto>>> searchProducts(@RequestParam String searchValue){
        System.out.println("searchValue = " + searchValue);
        ResponseEntity<ApiResponse<List<ProductDto>>> res4 = ResponseEntity.ok(
                ApiResponse.success("Result Fetched Successfully",this.productService.productSearch(searchValue)));
        System.out.println("res4 "+res4);
        return res4;
    }
    @PutMapping
    public ResponseEntity<ApiResponse<ProductDto>> listProductById(@RequestBody UpdateProductDto updateProductPayload){
        ResponseEntity<ApiResponse<ProductDto>> res3 = ResponseEntity.ok(
                ApiResponse.success("Product Updated Successfully",this.productService.updateProduct(updateProductPayload)));
        System.out.println("res3 "+res3);
        return res3;
    }
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ApiResponse<ProductDto>> updateStockQuantity(@PathVariable Long productId, @RequestBody @Valid UpdateStockDto payload){
        return ResponseEntity.ok(ApiResponse.success("Stock Quantity Updated Successfully",this.productService.updateStock(productId,payload)));
    }

}
