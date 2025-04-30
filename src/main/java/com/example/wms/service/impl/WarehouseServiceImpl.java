package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Warehouse;
import com.example.wms.model.db.repository.WarehouseRepository;
import com.example.wms.model.dto.request.WarehouseInfoReq;
import com.example.wms.model.dto.response.WarehouseInfoResp;
import com.example.wms.service.WarehouseService;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public WarehouseInfoResp createWarehouse(WarehouseInfoReq req) {
        if (warehouseRepository.findWarehouseByName(req.getName()).isPresent()) {
            throw new CommonBackendException("Warehouse with name already exists", HttpStatus.CONFLICT);
        }

        Warehouse warehouse = objectMapper.convertValue(req, Warehouse.class);
        warehouse.setIsActive(true);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return objectMapper.convertValue(savedWarehouse, WarehouseInfoResp.class);
    }


    @Override
    @Transactional(readOnly = true)
    public WarehouseInfoResp getWarehouse(Long id) {
        final String errMsg = String.format("Warehouse with id: %s not found", id);

        Warehouse warehouse = warehouseRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        return objectMapper.convertValue(warehouse, WarehouseInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseInfoResp> getAllWarehouses(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Warehouse> warehouses = warehouseRepository.findAllByIsActiveTrue(pageRequest);

        List<WarehouseInfoResp> content = warehouses.getContent().stream()
                .map(warehouse -> objectMapper.convertValue(warehouse, WarehouseInfoResp.class))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, warehouses.getTotalElements());
    }

    @Override
    @Transactional
    public WarehouseInfoResp updateWarehouse(Long id, WarehouseInfoReq req) {
        final String errMsg = String.format("Warehouse with id: %s not found", id);

        Warehouse warehouse = warehouseRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        warehouse.setName(req.getName() != null ? req.getName() : warehouse.getName());
        warehouse.setAddress(req.getAddress() != null ? req.getAddress() : warehouse.getAddress());
        warehouse.setCapacity(req.getCapacity() != null ? req.getCapacity() : warehouse.getCapacity());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return objectMapper.convertValue(updatedWarehouse, WarehouseInfoResp.class);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        final String errMsg = String.format("Warehouse with id: %s not found", id);

        Warehouse warehouse = warehouseRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);
    }
}
