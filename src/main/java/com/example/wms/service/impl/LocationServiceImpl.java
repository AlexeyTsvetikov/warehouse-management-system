package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Location;
import com.example.wms.model.db.entity.Warehouse;
import com.example.wms.model.db.repository.LocationRepository;
import com.example.wms.model.db.repository.WarehouseRepository;
import com.example.wms.model.dto.request.LocationInfoReq;
import com.example.wms.model.dto.response.LocationInfoResp;
import com.example.wms.service.LocationService;
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
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public LocationInfoResp createLocation(LocationInfoReq req) {
        if (locationRepository.findByName(req.getName()).isPresent()) {
            throw new CommonBackendException("Location  with name already exists", HttpStatus.CONFLICT);
        }

        Location location = objectMapper.convertValue(req, Location.class);

        if (req.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(req.getWarehouseId())
                    .orElseThrow(() -> new CommonBackendException(
                            "Warehouse with id: " + req.getWarehouseId() + " not found", HttpStatus.NOT_FOUND));
            location.setWarehouse(warehouse);
        } else {
            throw new CommonBackendException("Warehouse must be provided", HttpStatus.BAD_REQUEST);
        }
        location.setIsActive(true);
        Location savedLocation = locationRepository.save(location);

        return objectMapper.convertValue(savedLocation, LocationInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationInfoResp getLocation(Long id) {
        final String errMsg = String.format("Location with id: %s not found", id);

        Location location = locationRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        LocationInfoResp resp = objectMapper.convertValue(location, LocationInfoResp.class);
        resp.setWarehouseName(location.getWarehouse().getName());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocationInfoResp> getAllLocations(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Location> locations = locationRepository.findAllByIsActiveTrue(pageRequest);

        List<LocationInfoResp> content = locations.getContent().stream()
                .map(location -> {
                    LocationInfoResp resp = objectMapper.convertValue(location, LocationInfoResp.class);
                    resp.setWarehouseName(location.getWarehouse().getName());
                    return resp;
                }).collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, locations.getTotalElements());
    }

    @Override
    @Transactional
    public LocationInfoResp updateLocation(Long id, LocationInfoReq req) {
        final String errMsg = String.format("Location with id: %s not found", id);

        Location location = locationRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        if (req.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(req.getWarehouseId())
                    .orElseThrow(() -> new CommonBackendException(
                            "Warehouse with id : " + req.getWarehouseId() + " not found", HttpStatus.NOT_FOUND));
            location.setWarehouse(warehouse);
        }

        location.setName(req.getName() != null ? req.getName() : location.getName());
        location.setLocationType(req.getLocationType() != null ? req.getLocationType() : location.getLocationType());
        location.setMaxCapacity(req.getMaxCapacity() != null ? req.getMaxCapacity() : location.getMaxCapacity());
        location.setDimensions(req.getDimensions() != null ? req.getDimensions() : location.getDimensions());
        location.setDescription(req.getDescription() != null ? req.getDescription() : location.getDescription());

        Location updatedLocation = locationRepository.save(location);
        LocationInfoResp resp = objectMapper.convertValue(updatedLocation, LocationInfoResp.class);
        resp.setWarehouseName(updatedLocation.getWarehouse().getName());
        return resp;
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        final String errMsg = String.format("Location with id: %s not found", id);

        Location location = locationRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        location.setIsActive(false);
        locationRepository.save(location);
    }

}
