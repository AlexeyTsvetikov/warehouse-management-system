package com.example.wms.service;

import com.example.wms.model.dto.request.ManufacturerInfoReq;
import com.example.wms.model.dto.response.ManufacturerInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface ManufacturerService {
    @Transactional
    ManufacturerInfoResp createManufacturer(ManufacturerInfoReq req);

    @Transactional(readOnly = true)
    ManufacturerInfoResp getManufacturer(Long id);

    @Transactional(readOnly = true)
    Page<ManufacturerInfoResp> getAllManufacturers(Integer page, Integer perPage, String sort, Sort.Direction order);

    @Transactional
    ManufacturerInfoResp updateManufacturer(Long id, ManufacturerInfoReq req);

    @Transactional
    void deleteManufacturer(Long id);
}
