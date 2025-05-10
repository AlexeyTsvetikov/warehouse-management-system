package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Inventory;
import com.example.wms.model.db.entity.Stock;
import com.example.wms.model.db.repository.InventoryRepository;
import com.example.wms.model.dto.response.InventorInfoResp;
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
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InventorServiceImplTest {

    @InjectMocks
    private InventorServiceImpl inventorService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Spy
    private ObjectMapper objectMapper;


    @Test
    void getInventoryExists() {
        Stock stock = new Stock();
        stock.setId(2L);

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setStock(stock);
        inventory.setActualQuantity(100);

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));

        InventorInfoResp expectedResp = new InventorInfoResp();
        expectedResp.setId(inventory.getId());
        expectedResp.setActualQuantity(inventory.getActualQuantity());
        expectedResp.setStockId(inventory.getStock().getId());
        when(objectMapper.convertValue(inventory, InventorInfoResp.class)).thenReturn(expectedResp);

        InventorInfoResp resp = inventorService.getInventory(inventory.getId());

        assertEquals(expectedResp.getStockId(), resp.getStockId());
        assertEquals(expectedResp.getActualQuantity(), resp.getActualQuantity());
        assertEquals(expectedResp.getId(), resp.getId());
    }

    @Test
    void getInventoryNotExist() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        String expectedErrorMessage = String.format("Inventory with id: %s not found", inventory.getId());

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                inventorService.getInventory(inventory.getId()));
        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getAllInventoriesWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Long filter = 1L;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Stock stock = new Stock();
        stock.setId(1L);

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setStock(stock);

        InventorInfoResp inventorInfoResp = new InventorInfoResp();

        when(inventoryRepository.findAllFiltered(filter, pageable)).thenReturn(new PageImpl<>(List.of(inventory)));
        when(objectMapper.convertValue(inventory, InventorInfoResp.class)).thenReturn(inventorInfoResp);

        Page<InventorInfoResp> result = inventorService.getAllInventories(pageNumber, pageSize, sortField, sortDirection, filter);

        assertEquals(1, result.getContent().size());
        assertEquals(inventorInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllInventoriesWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Stock stock = new Stock();
        stock.setId(1L);

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setStock(stock);

        InventorInfoResp inventorInfoResp = new InventorInfoResp();

        when(inventoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(inventory)));
        when(objectMapper.convertValue(inventory, InventorInfoResp.class)).thenReturn(inventorInfoResp);

        Page<InventorInfoResp> result = inventorService.getAllInventories(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(inventorInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }
}