package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Inventory;
import com.example.wms.model.db.repository.InventoryRepository;
import com.example.wms.model.dto.response.InventorInfoResp;
import com.example.wms.service.InventoryService;
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
public class InventorServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public InventorInfoResp getInventory(Long id) {
        final String errMsg = String.format("Inventory with id: %s not found", id);

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));



        InventorInfoResp resp = objectMapper.convertValue(inventory, InventorInfoResp.class);
        resp.setStockId(inventory.getStock().getId());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventorInfoResp> getAllInventories(Integer page, Integer perPage, String sort, Sort.Direction order, Long stockId) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Inventory> inventories;

        if (stockId != null) {
            inventories = inventoryRepository.findAllFiltered(stockId, pageRequest);
        } else {
            inventories = inventoryRepository.findAll(pageRequest);
        }

        List<InventorInfoResp> content = inventories.getContent().stream()
                .map(inventory -> {
                    InventorInfoResp resp = objectMapper.convertValue(inventory, InventorInfoResp.class);
                    resp.setStockId(inventory.getStock().getId());
                    return resp;})
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageRequest, inventories.getTotalElements());
    }
}
