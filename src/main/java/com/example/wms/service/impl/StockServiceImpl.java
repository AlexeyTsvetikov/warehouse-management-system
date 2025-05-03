package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.*;
import com.example.wms.model.db.repository.*;
import com.example.wms.model.dto.response.StockInfoResp;
import com.example.wms.model.enums.StockStatus;
import com.example.wms.service.StockService;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Stock increaseStock(Product product, Location location, Integer quantity) {
        if (product == null || location == null || quantity == null || quantity <= 0) {
            throw new CommonBackendException("Invalid input parameters for increaseStock", HttpStatus.BAD_REQUEST);
        }

        Optional<Stock> existingStock = stockRepository.findByProductAndLocation(product, location);

        Stock stock;
        if (existingStock.isPresent()) {
            stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + quantity);
        } else {
            stock = new Stock();
            stock.setProduct(product);
            stock.setLocation(location);
            stock.setStatus(StockStatus.AVAILABLE);
            stock.setQuantity(quantity);
        }

        return stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void decreaseStock(Product product, Location location, Integer quantity) {
        if (product == null || location == null || quantity == null || quantity <= 0) {
            throw new CommonBackendException("Invalid input parameters for decreaseStock", HttpStatus.BAD_REQUEST);
        }

        Optional<Stock> existingStock = stockRepository.findByProductAndLocation(product, location);

        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();

            int newQuantity = stock.getQuantity() - quantity;

            if (newQuantity < 0) {
                throw new CommonBackendException("Insufficient stock to decrease", HttpStatus.BAD_REQUEST);
            }

            if (newQuantity == 0) {
                stockRepository.delete(stock);
                log.info("Stock for product {} at location {} has been deleted due to quantity reaching zero", product.getId(), location.getId());
            } else {
                stock.setQuantity(newQuantity);
                stockRepository.save(stock);
                log.info("Decreased stock for product {} at location {} by {}", product.getId(), location.getId(), quantity);
            }
        } else {
            final String errMsg = String.format("Stock not found for product with id: %s and location with id: %s", product.getId(), location.getId());
            throw new CommonBackendException(errMsg, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void transferStock(Product product, Integer quantity, Location fromLocation, Location toLocation) {
        if (fromLocation.equals(toLocation)) {
            throw new CommonBackendException("Source and destination locations cannot be the same", HttpStatus.BAD_REQUEST);
        }

        decreaseStock(product, fromLocation, quantity);
        increaseStock(product, toLocation, quantity);
    }

    @Override
    @Transactional(readOnly = true)
    public StockInfoResp getStockByProductAndLocation(Long productId, Long locationId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CommonBackendException("Product not found", HttpStatus.NOT_FOUND));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new CommonBackendException("Location not found", HttpStatus.NOT_FOUND));

        Stock stock = stockRepository.findByProductAndLocation(product, location)
                .orElseThrow(() -> new CommonBackendException("Stock not found", HttpStatus.NOT_FOUND));

        return getStockInfoResp(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockInfoResp> getAllStocks(Integer page, Integer perPage, String sort, Sort.Direction order, String filter) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Stock> stocks;
        if (StringUtils.hasText(filter)) {
            stocks = stockRepository.findAllFiltered(pageRequest, filter);
        } else {
            stocks = stockRepository.findAll(pageRequest);
        }

        List<StockInfoResp> content = stocks.getContent().stream()
                .map((this::getStockInfoResp))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, stocks.getTotalElements());
    }

    private StockInfoResp getStockInfoResp(Stock stock) {
        StockInfoResp resp = objectMapper.convertValue(stock, StockInfoResp.class);
        resp.setProductSku(stock.getProduct().getSku());
        resp.setLocationName(stock.getLocation().getName());
        return resp;
    }
}




