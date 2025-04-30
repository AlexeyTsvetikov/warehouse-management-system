package com.example.wms.service;

import com.example.wms.model.dto.request.WarehouseInfoReq;
import com.example.wms.model.dto.response.WarehouseInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;



public interface WarehouseService {
    @Transactional
    WarehouseInfoResp createWarehouse(WarehouseInfoReq request);

    @Transactional(readOnly = true)
    WarehouseInfoResp getWarehouse(Long id);

    @Transactional(readOnly = true)
    Page<WarehouseInfoResp> getAllWarehouses(Integer page, Integer perPage, String sort, Sort.Direction order);

    @Transactional
    WarehouseInfoResp updateWarehouse(Long id, WarehouseInfoReq request);

    @Transactional
    void deleteWarehouse(Long id);
}
