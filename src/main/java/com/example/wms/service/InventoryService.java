package com.example.wms.service;

import com.example.wms.model.dto.response.InventorInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public interface InventoryService {
    @Transactional(readOnly = true)
    InventorInfoResp getInventory(Long id);

    @Transactional(readOnly = true)
    Page<InventorInfoResp> getAllInventories(Integer page, Integer perPage, String sort, Sort.Direction order, Long stockId);
}
