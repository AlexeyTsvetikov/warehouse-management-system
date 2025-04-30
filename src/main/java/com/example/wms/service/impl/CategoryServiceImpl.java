package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Category;
import com.example.wms.model.db.repository.CategoryRepository;
import com.example.wms.model.dto.request.CategoryInfoReq;
import com.example.wms.model.dto.response.CategoryInfoResp;
import com.example.wms.service.CategoryService;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public CategoryInfoResp createCategory(CategoryInfoReq req) {
        if (categoryRepository.findByName(req.getName()).isPresent()) {
            throw new CommonBackendException("Category with name already exists", HttpStatus.CONFLICT);
        }

        Category category = objectMapper.convertValue(req, Category.class);
        category.setIsActive(true);

        Category savedCategory = categoryRepository.save(category);

        return objectMapper.convertValue(savedCategory, CategoryInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryInfoResp getCategory(Long id) {
        final String errMsg = String.format("Category with id: %s not found", id);

        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        return objectMapper.convertValue(category, CategoryInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryInfoResp> getAllCategories(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Category> categories = categoryRepository.findAllByIsActiveTrue(pageRequest);

        List<CategoryInfoResp> content = categories.getContent().stream()
                .map(category -> objectMapper.convertValue(category, CategoryInfoResp.class))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, categories.getTotalElements());
    }

    @Override
    @Transactional
    public CategoryInfoResp updateCategory(Long id, CategoryInfoReq req) {
        final String errMsg = String.format("Category with id: %s not found", id);

        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        category.setName(req.getName() != null ? req.getName() : category.getName());
        category.setDescription(req.getDescription() != null ? req.getDescription() : category.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return objectMapper.convertValue(updatedCategory, CategoryInfoResp.class);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        final String errMsg = String.format("Category with id: %s not found", id);

        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        category.setIsActive(false);
        categoryRepository.save(category);
    }
}
