package com.example.wms.controllers;

import com.example.wms.model.dto.response.StockInfoResp;

import com.example.wms.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/{productId}/{locationId}")
    public StockInfoResp getStockByProductAndLocation(@PathVariable Long productId, @PathVariable Long locationId) {
        return stockService.getStockByProductAndLocation(productId, locationId);
    }

    @GetMapping("/product/{productId}")
    public Page<StockInfoResp> getStockByProduct(@PathVariable Long productId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer perPage,
                                                 @RequestParam(defaultValue = "status") String sort,
                                                 @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return stockService.getStockByProduct(productId, page, perPage, sort, order);
    }

    @GetMapping("/location/{locationId}")
    public Page<StockInfoResp> getStockByLocation(@PathVariable Long locationId,
                                                     @RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer perPage,
                                                     @RequestParam(defaultValue = "status") String sort,
                                                     @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return stockService.getStockByLocation(locationId, page, perPage, sort, order);
    }

    @GetMapping("/all")
    public Page<StockInfoResp> getAllStocks(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer perPage,
                                            @RequestParam(defaultValue = "status") String sort,
                                            @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return stockService.getAllStocks(page, perPage, sort, order);
    }
}
