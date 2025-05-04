package com.example.wms.controllers;

import com.example.wms.model.dto.request.ProductInfoReq;
import com.example.wms.model.dto.response.ProductInfoResp;
import com.example.wms.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Товары")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Создать товар")
    public ProductInfoResp createProduct(@RequestBody @Valid ProductInfoReq req) {
        return productService.createProduct(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по id")
    public ProductInfoResp getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список товаров")
    public Page<ProductInfoResp> getAllProducts(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer perPage,
                                                @RequestParam(defaultValue = "sku") String sort,
                                                @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                @RequestParam(required = false) String filter) {
        return productService.getAllProducts(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар по id")
    public ProductInfoResp updateProduct(@PathVariable Long id, @RequestBody @Valid ProductInfoReq req) {
        return productService.updateProduct(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар по id")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
