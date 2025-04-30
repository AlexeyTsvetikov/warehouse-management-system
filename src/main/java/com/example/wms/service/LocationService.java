package com.example.wms.service;

import com.example.wms.model.dto.request.LocationInfoReq;
import com.example.wms.model.dto.response.LocationInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface LocationService {
    @Transactional
    LocationInfoResp createLocation(LocationInfoReq req);

    @Transactional(readOnly = true)
    LocationInfoResp getLocation(Long id);

    @Transactional(readOnly = true)
    Page<LocationInfoResp> getAllLocations(Integer page, Integer perPage, String sort, Sort.Direction order);

    @Transactional
    LocationInfoResp updateLocation(Long id, LocationInfoReq req);

    @Transactional
    void deleteLocation(Long id);
}
