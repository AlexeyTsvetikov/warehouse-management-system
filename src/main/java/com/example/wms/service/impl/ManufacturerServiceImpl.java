package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Manufacturer;
import com.example.wms.model.db.repository.ManufacturerRepository;
import com.example.wms.model.dto.request.ManufacturerInfoReq;
import com.example.wms.model.dto.response.ManufacturerInfoResp;
import com.example.wms.service.ManufacturerService;
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
public class ManufacturerServiceImpl implements ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ManufacturerInfoResp createManufacturer(ManufacturerInfoReq req) {
        if (manufacturerRepository.findByName(req.getName()).isPresent()) {
            throw new CommonBackendException("Manufacturer with name already exists", HttpStatus.CONFLICT);
        }

        Manufacturer manufacturer = objectMapper.convertValue(req, Manufacturer.class);
        manufacturer.setIsActive(true);

        Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);
        return objectMapper.convertValue(savedManufacturer, ManufacturerInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ManufacturerInfoResp getManufacturer(Long id) {
        final String errMsg = String.format("Manufacturer with id: %s not found", id);

        Manufacturer manufacturer = manufacturerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        return objectMapper.convertValue(manufacturer, ManufacturerInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ManufacturerInfoResp> getAllManufacturers(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Manufacturer> manufacturers = manufacturerRepository.findAllByIsActiveTrue(pageRequest);

        List<ManufacturerInfoResp> content = manufacturers.getContent().stream()
                .map(manufacturer -> objectMapper.convertValue(manufacturer, ManufacturerInfoResp.class))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, manufacturers.getTotalElements());
    }

    @Override
    @Transactional
    public ManufacturerInfoResp updateManufacturer(Long id, ManufacturerInfoReq req) {
        final String errMsg = String.format("Manufacturer with id: %s not found", id);

        Manufacturer manufacturer = manufacturerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        manufacturer.setName(req.getName() != null ? req.getName() : manufacturer.getName());
        manufacturer.setAddress(req.getAddress() != null ? req.getAddress() : manufacturer.getAddress());
        manufacturer.setEmail(req.getEmail() != null ? req.getEmail() : manufacturer.getEmail());
        manufacturer.setPhone(req.getPhone() != null ? req.getPhone() : manufacturer.getPhone());

        Manufacturer updatedManufacturer = manufacturerRepository.save(manufacturer);
        return objectMapper.convertValue(updatedManufacturer, ManufacturerInfoResp.class);
    }

    @Override
    @Transactional
    public void deleteManufacturer(Long id) {
        final String errMsg = String.format("Manufacturer with id: %s not found", id);

        Manufacturer manufacturer = manufacturerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        manufacturer.setIsActive(false);
        manufacturerRepository.save(manufacturer);
    }
}
