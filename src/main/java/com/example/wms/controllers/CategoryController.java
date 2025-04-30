package com.example.wms.controllers;

import com.example.wms.model.dto.request.CategoryInfoReq;
import com.example.wms.model.dto.response.CategoryInfoResp;
import com.example.wms.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryInfoResp createCategory(@RequestBody CategoryInfoReq req) {
        return categoryService.createCategory(req);
    }

    @GetMapping("/{id}")
    public CategoryInfoResp getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @GetMapping("/all")
    public Page<CategoryInfoResp> getAllCategories(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer perPage,
                                                    @RequestParam(defaultValue = "name") String sort,
                                                    @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return categoryService.getAllCategories(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    public CategoryInfoResp updateCategory(@PathVariable Long id, @RequestBody CategoryInfoReq req) {
        return categoryService.updateCategory(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
