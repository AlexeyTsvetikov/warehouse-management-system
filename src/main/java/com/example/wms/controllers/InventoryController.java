package com.example.wms.controllers;

import com.example.wms.model.dto.response.InventorInfoResp;
import com.example.wms.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
@Tag(name = "Инвентаризации")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить инвентаризацию запаса по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public InventorInfoResp getInventory(@PathVariable Long id) {
        return inventoryService.getInventory(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список инвентаризаций запасов")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public Page<InventorInfoResp> getAllInventories(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer perPage,
                                                    @RequestParam(defaultValue = "id") String sort,
                                                    @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                    @RequestParam(required = false) Long filter) {
        return inventoryService.getAllInventories(page, perPage, sort, order, filter);
    }
}
