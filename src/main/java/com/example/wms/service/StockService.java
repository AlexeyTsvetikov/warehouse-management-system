package com.example.wms.service;


import com.example.wms.model.db.entity.Location;
import com.example.wms.model.db.entity.Product;
import com.example.wms.model.db.entity.Stock;
import com.example.wms.model.dto.response.StockInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public interface StockService {

    @Transactional
    Stock increaseStock(Product product, Location location, Integer quantity);

    @Transactional
    void decreaseStock(Product product, Location location, Integer quantity);

    @Transactional
    void transferStock(Product product, Integer quantity, Location fromLocation, Location toLocation);

    @Transactional
    void stockInventory(Long stockId, Integer actualQuantity);

    @Transactional
    StockInfoResp updateQuantity(Long stockId, Integer quantity);

    @Transactional(readOnly = true)
    StockInfoResp getStockByProductAndLocation(Long productId, Long locationId);

    @Transactional(readOnly = true)
    Page<StockInfoResp> getAllStocks(Integer page, Integer perPage, String sort, Sort.Direction order, String filter);


}
