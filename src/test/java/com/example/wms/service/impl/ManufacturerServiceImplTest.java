package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Manufacturer;
import com.example.wms.model.db.repository.ManufacturerRepository;
import com.example.wms.model.dto.request.ManufacturerInfoReq;
import com.example.wms.model.dto.response.ManufacturerInfoResp;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceImplTest {

    @InjectMocks
    private ManufacturerServiceImpl manufacturerService;

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createManufacturer() {
        ManufacturerInfoReq req = new ManufacturerInfoReq();
        req.setName("TestName");
        req.setAddress("TestAddress");
        req.setPhone("TestPhone");
        req.setEmail("TestEmail");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(req.getName());
        manufacturer.setAddress(req.getAddress());
        manufacturer.setEmail(req.getEmail());
        manufacturer.setPhone(req.getPhone());

        when(manufacturerRepository.findByName(req.getName())).thenReturn(Optional.empty());

        when(manufacturerRepository.save(any(Manufacturer.class))).thenReturn(manufacturer);

        ManufacturerInfoResp manufacturerInfoResp = manufacturerService.createManufacturer(req);

        assertEquals(manufacturer.getName(), manufacturerInfoResp.getName());
        assertEquals(manufacturer.getAddress(), manufacturerInfoResp.getAddress());
        assertEquals(manufacturer.getPhone(), manufacturerInfoResp.getPhone());
        assertEquals(manufacturer.getEmail(), manufacturerInfoResp.getEmail());
    }

    @Test
    void createManufacturerExists() {
        ManufacturerInfoReq req = new ManufacturerInfoReq();
        req.setName("ExistingName");

        Manufacturer existingManufacturer = new Manufacturer();
        existingManufacturer.setName("ExistingName");

        when(manufacturerRepository.findByName(req.getName())).thenReturn(Optional.of(existingManufacturer));

        assertThrows(CommonBackendException.class, () -> manufacturerService.createManufacturer(req));
    }

    @Test
    void getManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("TestName");
        manufacturer.setAddress("TestAddress");
        manufacturer.setPhone("TestPhone");
        manufacturer.setEmail("TestEmail");
        manufacturer.setIsActive(true);

        when(manufacturerRepository.findByIdAndIsActiveTrue(manufacturer.getId())).thenReturn(Optional.of(manufacturer));

        ManufacturerInfoResp manufacturerInfoResp = manufacturerService.getManufacturer(manufacturer.getId());
        assertEquals(manufacturer.getId(), manufacturerInfoResp.getId());
        assertEquals(manufacturer.getName(), manufacturerInfoResp.getName());
        assertEquals(manufacturer.getAddress(), manufacturerInfoResp.getAddress());
        assertEquals(manufacturer.getPhone(), manufacturerInfoResp.getPhone());
        assertEquals(manufacturer.getEmail(), manufacturerInfoResp.getEmail());
    }

    @Test
    void getManufacturerNotFound() {
        Long nonExistingId = 999L;
        assertManufacturerNotFound(nonExistingId);
    }

    @Test
    void getAllManufacturers() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Manufacturer manufacturer = new Manufacturer();
        ManufacturerInfoResp manufacturerInfoResp = new ManufacturerInfoResp();

        when(manufacturerRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(manufacturer)));
        when(objectMapper.convertValue(manufacturer, ManufacturerInfoResp.class)).thenReturn(manufacturerInfoResp);

        Page<ManufacturerInfoResp> result = manufacturerService.getAllManufacturers(pageNumber, pageSize, sortField, sortDirection);

        assertEquals(1, result.getContent().size());
        assertEquals(manufacturerInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateManufacturerAllValues() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("TestName");
        manufacturer.setAddress("TestAddress");
        manufacturer.setPhone("TestPhone");
        manufacturer.setEmail("TestEmail");
        manufacturer.setIsActive(true);

        ManufacturerInfoReq req = new ManufacturerInfoReq();
        req.setName("NewName");
        req.setAddress("NewAddress");
        req.setEmail("NewEmail");
        req.setPhone("NewPhone");

        when(manufacturerRepository.findByIdAndIsActiveTrue(manufacturer.getId())).thenReturn(Optional.of(manufacturer));

        when(manufacturerRepository.save(any(Manufacturer.class))).thenReturn(manufacturer);

        ManufacturerInfoResp expectedResponse = new ManufacturerInfoResp();
        expectedResponse.setId(manufacturer.getId());
        expectedResponse.setName(req.getName());
        expectedResponse.setAddress(req.getAddress());
        expectedResponse.setEmail(req.getEmail());
        expectedResponse.setPhone(req.getPhone());
        when(objectMapper.convertValue(manufacturer, ManufacturerInfoResp.class)).thenReturn(expectedResponse);

        ManufacturerInfoResp resp = manufacturerService.updateManufacturer(manufacturer.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getAddress(), resp.getAddress());
        assertEquals(expectedResponse.getEmail(), resp.getEmail());
        assertEquals(expectedResponse.getPhone(), resp.getPhone());
    }

    @Test
    void updateManufacturerNullValues() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("TestName");
        manufacturer.setAddress("TestAddress");
        manufacturer.setPhone("TestPhone");
        manufacturer.setEmail("TestEmail");
        manufacturer.setIsActive(true);

        ManufacturerInfoReq req = new ManufacturerInfoReq();
        req.setName(null);
        req.setAddress(null);
        req.setEmail(null);
        req.setPhone(null);

        when(manufacturerRepository.findByIdAndIsActiveTrue(manufacturer.getId())).thenReturn(Optional.of(manufacturer));

        when(manufacturerRepository.save(any(Manufacturer.class))).thenReturn(manufacturer);

        ManufacturerInfoResp expectedResponse = new ManufacturerInfoResp();
        expectedResponse.setId(manufacturer.getId());
        expectedResponse.setName(manufacturer.getName());
        expectedResponse.setAddress(manufacturer.getAddress());
        expectedResponse.setEmail(manufacturer.getEmail());
        expectedResponse.setPhone(manufacturer.getPhone());
        when(objectMapper.convertValue(manufacturer, ManufacturerInfoResp.class)).thenReturn(expectedResponse);

        ManufacturerInfoResp resp = manufacturerService.updateManufacturer(manufacturer.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getAddress(), resp.getAddress());
        assertEquals(expectedResponse.getEmail(), resp.getEmail());
        assertEquals(expectedResponse.getPhone(), resp.getPhone());
    }

    @Test
    void updateManufacturerNotFound() {
        Long nonExistingId = 999L;
        assertManufacturerNotFound(nonExistingId);
    }

    @Test
    void deleteManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        when(manufacturerRepository.findByIdAndIsActiveTrue(manufacturer.getId())).thenReturn(Optional.of(manufacturer));

        manufacturerService.deleteManufacturer(manufacturer.getId());
        verify(manufacturerRepository, times(1)).save(any(Manufacturer.class));
        assertEquals(false, manufacturer.getIsActive());
    }

    @Test
    void deleteManufacturerNotFound() {
        Long nonExistingId = 999L;
        assertManufacturerNotFound(nonExistingId);
    }

    private void assertManufacturerNotFound(Long manufacturerId) {
        when(manufacturerRepository.findByIdAndIsActiveTrue(manufacturerId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> manufacturerService.getManufacturer(manufacturerId));
    }
}