package com.example.wms.controllers;

import com.example.wms.model.dto.request.WarehouseInfoReq;
import com.example.wms.model.dto.response.WarehouseInfoResp;
import com.example.wms.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@Tag(name = "Склады")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping
    @Operation(summary = "Создать склад")
    public WarehouseInfoResp createWarehouse(@RequestBody @Valid WarehouseInfoReq req) {
        return warehouseService.createWarehouse(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить склад по id")
    public WarehouseInfoResp getWarehouse(@PathVariable Long id) {
        return warehouseService.getWarehouse(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список складов")
    public Page<WarehouseInfoResp> getAllWarehouses(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer perPage,
                                                    @RequestParam(defaultValue = "name") String sort,
                                                    @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return warehouseService.getAllWarehouses(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить склад по id")
    public WarehouseInfoResp updateWarehouse(@PathVariable Long id, @RequestBody @Valid WarehouseInfoReq req) {
        return warehouseService.updateWarehouse(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить склад по id")
    public void deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
    }

}
