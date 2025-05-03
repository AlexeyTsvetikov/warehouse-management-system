package com.example.wms.service;

import com.example.wms.model.dto.request.ProductInfoReq;
import com.example.wms.model.dto.response.ProductInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface ProductService {
    ProductInfoResp createProduct(ProductInfoReq req);

    ProductInfoResp getProduct(Long id);

    @Transactional(readOnly = true)
    Page<ProductInfoResp> getAllProducts(Integer page, Integer perPage, String sort, Sort.Direction order, String filter);

    ProductInfoResp updateProduct(Long id, ProductInfoReq req);

    void deleteProduct(Long id);
}
