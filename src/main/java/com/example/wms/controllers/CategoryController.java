package com.example.wms.controllers;

import com.example.wms.model.dto.request.CategoryInfoReq;
import com.example.wms.model.dto.response.CategoryInfoResp;
import com.example.wms.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Категории товаров")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Создать категорию товаров")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public CategoryInfoResp createCategory(@RequestBody @Valid CategoryInfoReq req) {
        return categoryService.createCategory(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить категорию товаров по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public CategoryInfoResp getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список категорий товаров")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public Page<CategoryInfoResp> getAllCategories(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer perPage,
                                                   @RequestParam(defaultValue = "name") String sort,
                                                   @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return categoryService.getAllCategories(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить категорию товаров по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public CategoryInfoResp updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryInfoReq req) {
        return categoryService.updateCategory(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию товаров по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
