package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Warehouse;
import com.example.wms.model.db.repository.WarehouseRepository;
import com.example.wms.model.dto.request.WarehouseInfoReq;
import com.example.wms.model.dto.response.WarehouseInfoResp;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createWarehouse() {
        WarehouseInfoReq req = new WarehouseInfoReq();
        req.setName("TestName");
        req.setAddress("TestAddress");
        req.setCapacity(BigDecimal.valueOf(1000));

        Warehouse warehouse = new Warehouse();
        warehouse.setName(req.getName());
        warehouse.setAddress(req.getAddress());
        warehouse.setCapacity(req.getCapacity());

        when(warehouseRepository.findWarehouseByName(req.getName())).thenReturn(Optional.empty());

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseInfoResp warehouseInfoResp = warehouseService.createWarehouse(req);

        assertEquals(warehouse.getName(),warehouseInfoResp.getName());
        assertEquals(warehouse.getAddress(),warehouseInfoResp.getAddress());
        assertEquals(warehouse.getCapacity(),warehouseInfoResp.getCapacity());
    }

    @Test
    void createWarehouseExists() {
        WarehouseInfoReq req = new WarehouseInfoReq();
        req.setName("ExistingName");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setName("ExistingName");

        when(warehouseRepository.findWarehouseByName(req.getName())).thenReturn(Optional.of(existingWarehouse));

        assertThrows(CommonBackendException.class, () -> warehouseService.createWarehouse(req));
    }

    @Test
    void getWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("TestName");
        warehouse.setAddress("TestAddress");
        warehouse.setCapacity(BigDecimal.valueOf(1000));
        warehouse.setIsActive(true);

        when(warehouseRepository.findByIdAndIsActiveTrue(warehouse.getId())).thenReturn(Optional.of(warehouse));

        WarehouseInfoResp warehouseInfoResp = warehouseService.getWarehouse(warehouse.getId());
        assertEquals(warehouse.getId(), warehouseInfoResp.getId());
        assertEquals(warehouse.getName(), warehouseInfoResp.getName());
        assertEquals(warehouse.getAddress(), warehouseInfoResp.getAddress());
        assertEquals(warehouse.getCapacity(), warehouseInfoResp.getCapacity());
    }

    @Test
    void getWarehouseNotFound() {
        Long nonExistingId = 999L;
        assertWarehouseNotFound(nonExistingId);
    }

    @Test
    void getAllWarehouses() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        WarehouseInfoResp warehouseInfoResp = new WarehouseInfoResp();

        when(warehouseRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(warehouse)));
        when(objectMapper.convertValue(warehouse, WarehouseInfoResp.class)).thenReturn(warehouseInfoResp);

        Page<WarehouseInfoResp> result = warehouseService.getAllWarehouses(pageNumber, pageSize, sortField, sortDirection);

        assertEquals(1, result.getContent().size());
        assertEquals(warehouseInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateWarehouseAllValues() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("TestName");
        warehouse.setAddress("TestAddress");
        warehouse.setCapacity(BigDecimal.valueOf(1000));
        warehouse.setIsActive(true);

        WarehouseInfoReq req = new WarehouseInfoReq();
        req.setName("NewName");
        req.setAddress("NewAddress");
        req.setCapacity(BigDecimal.valueOf(1500));

        when(warehouseRepository.findByIdAndIsActiveTrue(warehouse.getId())).thenReturn(Optional.of(warehouse));

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseInfoResp expectedResponse = new WarehouseInfoResp();
        expectedResponse.setId(warehouse.getId());
        expectedResponse.setName(req.getName());
        expectedResponse.setAddress(req.getAddress());
        expectedResponse.setCapacity(req.getCapacity());
        when(objectMapper.convertValue(warehouse, WarehouseInfoResp.class)).thenReturn(expectedResponse);

        WarehouseInfoResp resp = warehouseService.updateWarehouse(warehouse.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getAddress(), resp.getAddress());
        assertEquals(expectedResponse.getCapacity(), resp.getCapacity());
    }

    @Test
    void updateWarehouseNullValues() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("TestName");
        warehouse.setAddress("TestAddress");
        warehouse.setCapacity(BigDecimal.valueOf(1000));
        warehouse.setIsActive(true);

        WarehouseInfoReq req = new WarehouseInfoReq();
        req.setName(null);
        req.setAddress(null);
        req.setCapacity(null);

        when(warehouseRepository.findByIdAndIsActiveTrue(warehouse.getId())).thenReturn(Optional.of(warehouse));

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseInfoResp expectedResponse = new WarehouseInfoResp();
        expectedResponse.setId(warehouse.getId());
        expectedResponse.setName(warehouse.getName());
        expectedResponse.setAddress(warehouse.getAddress());
        expectedResponse.setCapacity(warehouse.getCapacity());
        when(objectMapper.convertValue(warehouse, WarehouseInfoResp.class)).thenReturn(expectedResponse);

        WarehouseInfoResp resp = warehouseService.updateWarehouse(warehouse.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getAddress(), resp.getAddress());
        assertEquals(expectedResponse.getCapacity(), resp.getCapacity());
    }

    @Test
    void deleteWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        when(warehouseRepository.findByIdAndIsActiveTrue(warehouse.getId())).thenReturn(Optional.of(warehouse));

        warehouseService.deleteWarehouse(warehouse.getId());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
        assertEquals(false, warehouse.getIsActive());
    }

    @Test
    void deleteWarehouseNotFound() {
        Long nonExistingId = 999L;
        assertWarehouseNotFound(nonExistingId);
    }

    private void assertWarehouseNotFound(Long warehouseId) {
        when(warehouseRepository.findByIdAndIsActiveTrue(warehouseId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> warehouseService.getWarehouse(warehouseId));
    }
}