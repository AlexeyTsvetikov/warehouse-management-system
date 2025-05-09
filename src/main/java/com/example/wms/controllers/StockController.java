package com.example.wms.controllers;

import com.example.wms.model.dto.response.StockInfoResp;

import com.example.wms.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@Tag(name = "Запасы")
public class StockController {
    private final StockService stockService;

    @GetMapping("/{productId}/{locationId}")
    @Operation(summary = "Получить запас товара по id товара и id локации")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public StockInfoResp getStockByProductAndLocation(@PathVariable Long productId, @PathVariable Long locationId) {
        return stockService.getStockByProductAndLocation(productId, locationId);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список запасов товаров")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public Page<StockInfoResp> getAllStocks(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer perPage,
                                            @RequestParam(defaultValue = "status") String sort,
                                            @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                            @RequestParam(required = false) String filter) {
        return stockService.getAllStocks(page, perPage, sort, order, filter);
    }

    @PostMapping("/inventory/{stockId}/{actualQuantity}")
    @Operation(summary = "Провести инвентаризацию запаса по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public void stockInventory(@PathVariable Long stockId, @PathVariable Integer actualQuantity) {
        stockService.stockInventory(stockId, actualQuantity);
    }

    @PutMapping("/update/{stockId}/quantity")
    @Operation(summary = "Обновить количество запаса по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public StockInfoResp updateStockQuantity(@PathVariable Long stockId, @RequestParam Integer quantity) {
        return stockService.updateQuantity(stockId, quantity);
    }

}
