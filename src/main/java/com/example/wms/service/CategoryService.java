package com.example.wms.service;

import com.example.wms.model.dto.request.CategoryInfoReq;
import com.example.wms.model.dto.response.CategoryInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface CategoryService {

    @Transactional
    CategoryInfoResp createCategory(CategoryInfoReq req);

    @Transactional(readOnly = true)
    CategoryInfoResp getCategory(Long id);

    @Transactional(readOnly = true)
    Page<CategoryInfoResp> getAllCategories(Integer page, Integer perPage, String sort, Sort.Direction order);

    @Transactional
    CategoryInfoResp updateCategory(Long id, CategoryInfoReq req);

    @Transactional
    void deleteCategory(Long id);
}
