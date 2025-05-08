package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Location;
import com.example.wms.model.db.entity.Product;
import com.example.wms.model.db.entity.Stock;
import com.example.wms.model.db.repository.LocationRepository;
import com.example.wms.model.db.repository.ProductRepository;
import com.example.wms.model.db.repository.StockRepository;
import com.example.wms.model.dto.response.StockInfoResp;
import com.example.wms.model.enums.StockStatus;
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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LocationRepository locationRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void increaseStockWithExistingStock() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = 10;

        Stock existingStock = new Stock();
        existingStock.setQuantity(5);

        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        Stock result = stockService.increaseStock(product, location, quantity);

        assertEquals(15, result.getQuantity());
        verify(stockRepository).findByProductAndLocation(product, location);
        verify(stockRepository).save(existingStock);
    }

    @Test
    void increaseStockWithNewStock() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = 10;

        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.empty());
        Stock newStock = new Stock();
        newStock.setProduct(product);
        newStock.setLocation(location);
        newStock.setStatus(StockStatus.AVAILABLE);
        newStock.setQuantity(quantity);

        when(stockRepository.save(any(Stock.class))).thenReturn(newStock);

        Stock result = stockService.increaseStock(product, location, quantity);

        assertEquals(newStock.getQuantity(), result.getQuantity());
        assertEquals(newStock.getStatus(), result.getStatus());
        verify(stockRepository).findByProductAndLocation(product, location);
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void increaseStockWithInvalidProduct() {
        Product product = null;
        Location location = new Location();
        Integer quantity = 10;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.increaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for increaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void increaseStockWithInvalidLocation() {
        Product product = new Product();
        Location location = null;
        Integer quantity = 10;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.increaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for increaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void increaseStockWithNegativeQuantity() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = -5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.increaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for increaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void increaseStockWithNullQuantity() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = null;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.increaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for increaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void decreaseStockWithExistingStock() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = 5;

        Stock existingStock = new Stock();
        existingStock.setQuantity(10);

        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        stockService.decreaseStock(product, location, quantity);

        assertEquals(5, existingStock.getQuantity());
        verify(stockRepository).findByProductAndLocation(product, location);
        verify(stockRepository).save(existingStock);
    }

    @Test
    void decreaseStockWithNegativeQuantity() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = -5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.decreaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for decreaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void decreaseStockWithZeroQuantity() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = 10;

        Stock existingStock = new Stock();
        existingStock.setQuantity(10);

        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.of(existingStock));

        stockService.decreaseStock(product, location, quantity);

        verify(stockRepository).delete(existingStock);
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void decreaseStockWithInsufficientQuantity() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = 15;

        Stock existingStock = new Stock();
        existingStock.setQuantity(10);

        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.of(existingStock));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.decreaseStock(product, location, quantity));

        assertEquals("Insufficient stock to decrease", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void decreaseStockWithInvalidProduct() {
        Product product = null;
        Location location = new Location();
        Integer quantity = 5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.decreaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for decreaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void decreaseStockWithInvalidLocation() {
        Product product = new Product();
        Location location = null;
        Integer quantity = 5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.decreaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for decreaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void decreaseStockWithInvalidQuantity() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = null;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.decreaseStock(product, location, quantity));

        assertEquals("Invalid input parameters for decreaseStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void decreaseStockWithStockNotFound() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = 5;

        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.decreaseStock(product, location, quantity));

        assertEquals("Stock not found for product with id: null and location with id: null", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void transferStockSuccessfully() {
        Product product = new Product();
        Location fromLocation = new Location();
        Location toLocation = new Location();
        Integer quantity = 5;

        Stock fromStock = new Stock();
        fromStock.setQuantity(10);

        Stock toStock = new Stock();
        toStock.setQuantity(10);

        when(stockRepository.findByProductAndLocation(product, fromLocation)).thenReturn(Optional.of(fromStock));
        when(stockRepository.findByProductAndLocation(product, toLocation)).thenReturn(Optional.of(toStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(fromStock);

        stockService.transferStock(product, quantity, fromLocation, toLocation);

        assertEquals(5, fromStock.getQuantity());
        assertEquals(15, toStock.getQuantity());
        verify(stockRepository).findByProductAndLocation(product, fromLocation);
        verify(stockRepository).findByProductAndLocation(product, toLocation);
        verify(stockRepository, times(2)).save(any(Stock.class));
    }

    @Test
    void transferStockWithSameLocations() {
        Product product = new Product();
        Location location = new Location();
        Integer quantity = 5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.transferStock(product, quantity, location, location));

        assertEquals("Source and destination locations cannot be the same", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void transferStockWithInsufficientQuantity() {
        Product product = new Product();
        Location fromLocation = new Location();
        Location toLocation = new Location();
        Integer quantity = 15;

        Stock fromStock = new Stock();
        fromStock.setQuantity(10);

        when(stockRepository.findByProductAndLocation(product, fromLocation)).thenReturn(Optional.of(fromStock));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.transferStock(product, quantity, fromLocation, toLocation));

        assertEquals("Insufficient stock to decrease", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void transferStockWithInvalidProduct() {
        Product product = null;
        Location fromLocation = new Location();
        Location toLocation = new Location();
        Integer quantity = 5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.transferStock(product, quantity, fromLocation, toLocation));

        assertEquals("Invalid input parameters for transferStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void transferStockWithInvalidFromLocation() {
        Product product = new Product();
        Location fromLocation = null;
        Location toLocation = new Location();
        Integer quantity = 5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.transferStock(product, quantity, fromLocation, toLocation));

        assertEquals("Invalid input parameters for transferStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void transferStockWithInvalidToLocation() {
        Product product = new Product();
        Location fromLocation = new Location();
        Location toLocation = null;
        Integer quantity = 5;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.transferStock(product, quantity, fromLocation, toLocation));

        assertEquals("Invalid input parameters for transferStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void transferStockWithInvalidQuantity() {
        Product product = new Product();
        Location fromLocation = new Location();
        Location toLocation = new Location();
        Integer quantity = null;

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.transferStock(product, quantity, fromLocation, toLocation));

        assertEquals("Invalid input parameters for transferStock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getStockByProductAndLocationSuccess() {
        Long productId = 1L;
        Long locationId = 1L;

        Product product = new Product();
        product.setId(productId);

        Location location = new Location();
        location.setId(locationId);

        Stock stock = new Stock();
        stock.setStatus(StockStatus.AVAILABLE);
        stock.setProduct(product);
        stock.setLocation(location);
        stock.setQuantity(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.of(stock));

        StockInfoResp response = stockService.getStockByProductAndLocation(productId, locationId);

        assertEquals(stock.getQuantity(), response.getQuantity());
        assertEquals(stock.getStatus(), response.getStatus());
        assertEquals(stock.getLocation().getName(), response.getLocationName());

        verify(productRepository).findById(productId);
        verify(locationRepository).findById(locationId);
        verify(stockRepository).findByProductAndLocation(product, location);
    }

    @Test
    void getStockByProductAndLocationProductNotFound() {
        Long productId = 1L;
        Long locationId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.getStockByProductAndLocation(productId, locationId));

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getStockByProductAndLocationLocationNotFound() {
        Long productId = 1L;
        Long locationId = 1L;

        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.getStockByProductAndLocation(productId, locationId));

        assertEquals("Location not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getStockByProductAndLocationStockNotFound() {
        Long productId = 1L;
        Long locationId = 1L;

        Product product = new Product();
        product.setId(productId);

        Location location = new Location();
        location.setId(locationId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(stockRepository.findByProductAndLocation(product, location)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                stockService.getStockByProductAndLocation(productId, locationId));

        assertEquals("Stock not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getAllStocksWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String filter = "active";

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Product product = new Product();
        Location location = new Location();

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setLocation(location);
        StockInfoResp stockInfoResp = new StockInfoResp();

        when(stockRepository.findAllFiltered(pageable, filter)).thenReturn(new PageImpl<>(List.of(stock)));
        when(objectMapper.convertValue(stock, StockInfoResp.class)).thenReturn(stockInfoResp);

        Page<StockInfoResp> result = stockService.getAllStocks(pageNumber, pageSize, sortField, sortDirection, filter);

        assertEquals(1, result.getContent().size());
        assertEquals(stockInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
        verify(stockRepository).findAllFiltered(pageable, filter);
    }

    @Test
    void getAllStocksWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Product product = new Product();
        Location location = new Location();

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setLocation(location);
        StockInfoResp stockInfoResp = new StockInfoResp();

        when(stockRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(stock)));
        when(objectMapper.convertValue(stock, StockInfoResp.class)).thenReturn(stockInfoResp);

        Page<StockInfoResp> result = stockService.getAllStocks(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(stockInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
        verify(stockRepository).findAll(pageable);
    }
}