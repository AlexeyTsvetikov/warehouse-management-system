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

    @Transactional(readOnly = true)
    Page<StockInfoResp> getAllStocks(Integer page, Integer perPage, String sort, Sort.Direction order);

    @Transactional(readOnly = true)
    StockInfoResp getStockByProductAndLocation(Long productId, Long locationId);

    @Transactional(readOnly = true)
    Page<StockInfoResp> getStockByProduct(Long productId, Integer page, Integer perPage, String sort, Sort.Direction order);

    @Transactional(readOnly = true)
    Page<StockInfoResp> getStockByLocation(Long locationId, Integer page, Integer perPage, String sort, Sort.Direction order);
}
