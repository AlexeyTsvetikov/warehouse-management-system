package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Category;
import com.example.wms.model.db.repository.CategoryRepository;
import com.example.wms.model.dto.request.CategoryInfoReq;
import com.example.wms.model.dto.response.CategoryInfoResp;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createCategory() {
        CategoryInfoReq req = new CategoryInfoReq();
        req.setName("TestCategory");
        req.setDescription("TestDescription");

        when(categoryRepository.findByName(req.getName())).thenReturn(Optional.empty());

        Category category = new Category();
        category.setName(req.getName());
        category.setDescription(req.getDescription());

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryInfoResp categoryInfoResp = categoryService.createCategory(req);

        assertEquals(category.getId(), categoryInfoResp.getId());
        assertEquals(category.getName(), categoryInfoResp.getName());
        assertEquals(category.getDescription(), categoryInfoResp.getDescription());
    }

    @Test
    void createCategoryExists() {
        CategoryInfoReq req = new CategoryInfoReq();
        req.setName("ExistingCategory");

        Category existingCategory = new Category();
        existingCategory.setName("ExistingCategory");

        when(categoryRepository.findByName(req.getName())).thenReturn(Optional.of(existingCategory));

        assertThrows(CommonBackendException.class, () -> categoryService.createCategory(req));
    }

    @Test
    void getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("TestCategory");
        category.setDescription("TestDescription");
        category.setIsActive(true);


        when(categoryRepository.findByIdAndIsActiveTrue(category.getId())).thenReturn(Optional.of(category));

        CategoryInfoResp categoryInfoResp = categoryService.getCategory(category.getId());
        assertEquals(category.getId(), categoryInfoResp.getId());
        assertEquals(category.getName(), categoryInfoResp.getName());
        assertEquals(category.getDescription(), categoryInfoResp.getDescription());
    }

    @Test
    void getCategoryNotFound() {
        Long nonExistingId = 999L;
        assertCategoryNotFound(nonExistingId);
    }

    @Test
    void getAllCategories() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);
        Category category = new Category();
        CategoryInfoResp categoryInfoResp = new CategoryInfoResp();

        when(categoryRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(category)));
        when(objectMapper.convertValue(category, CategoryInfoResp.class)).thenReturn(categoryInfoResp);

        Page<CategoryInfoResp> result = categoryService.getAllCategories(pageNumber, pageSize, sortField, sortDirection);

        assertEquals(1, result.getContent().size());
        assertEquals(categoryInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateCategoryAllValues() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Old Name");
        category.setDescription("Old Description");
        category.setIsActive(true);

        CategoryInfoReq req = new CategoryInfoReq();
        req.setName("New Name");
        req.setDescription("New Description");

        when(categoryRepository.findByIdAndIsActiveTrue(category.getId())).thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryInfoResp expectedResponse = new CategoryInfoResp();
        expectedResponse.setId(category.getId());
        expectedResponse.setName(req.getName());
        expectedResponse.setDescription(req.getDescription());
        when(objectMapper.convertValue(category, CategoryInfoResp.class)).thenReturn(expectedResponse);

        CategoryInfoResp response = categoryService.updateCategory(category.getId(), req);

        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.getDescription(), response.getDescription());
    }

    @Test
    void updateCategoryNullValues() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Old Name");
        category.setDescription("Old Description");
        category.setIsActive(true);

        CategoryInfoReq req = new CategoryInfoReq();
        req.setName(null);
        req.setDescription(null);

        when(categoryRepository.findByIdAndIsActiveTrue(category.getId())).thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryInfoResp expectedResponse = new CategoryInfoResp();
        expectedResponse.setId(category.getId());
        expectedResponse.setName(category.getName());
        expectedResponse.setDescription(category.getDescription());
        when(objectMapper.convertValue(category, CategoryInfoResp.class)).thenReturn(expectedResponse);

        CategoryInfoResp response = categoryService.updateCategory(category.getId(), req);

        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.getDescription(), response.getDescription());
    }

    @Test
    void updateCategoryNotFound() {
        Long nonExistingId = 999L;
        assertCategoryNotFound(nonExistingId);
    }


    @Test
    void deleteCategory() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findByIdAndIsActiveTrue(category.getId())).thenReturn(Optional.of(category));

        categoryService.deleteCategory(category.getId());
        verify(categoryRepository, times(1)).save(any(Category.class));
        assertEquals(false, category.getIsActive());
    }

    @Test
    void deleteCategoryNotFound() {
        Long nonExistingId = 999L;
        assertCategoryNotFound(nonExistingId);
    }

    private void assertCategoryNotFound(Long categoryId) {
        when(categoryRepository.findByIdAndIsActiveTrue(categoryId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> categoryService.getCategory(categoryId));
    }
}