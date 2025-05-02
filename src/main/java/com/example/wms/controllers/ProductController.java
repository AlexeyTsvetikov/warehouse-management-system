package com.example.wms.controllers;

import com.example.wms.model.dto.request.ProductInfoReq;
import com.example.wms.model.dto.response.ProductInfoResp;
import com.example.wms.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ProductInfoResp createProduct(@RequestBody ProductInfoReq req) {
        return productService.createProduct(req);
    }

    @GetMapping("/{id}")
    public ProductInfoResp getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/all")
    public Page<ProductInfoResp> getAllProducts(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer perPage,
                                                    @RequestParam(defaultValue = "sku") String sort,
                                                    @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return productService.getAllProducts(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    public ProductInfoResp updateProduct(@PathVariable Long id, @RequestBody ProductInfoReq req) {
        return productService.updateProduct(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
