package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Location;
import com.example.wms.model.db.entity.Operation;
import com.example.wms.model.db.entity.OperationDetail;
import com.example.wms.model.db.entity.Product;
import com.example.wms.model.db.repository.LocationRepository;
import com.example.wms.model.db.repository.OperationDetailRepository;
import com.example.wms.model.db.repository.OperationRepository;
import com.example.wms.model.db.repository.ProductRepository;
import com.example.wms.model.dto.request.OperationDetailInfoReq;
import com.example.wms.model.dto.response.OperationDetailInfoResp;
import com.example.wms.model.enums.OperationStatus;
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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OperationDetailServiceImplTest {

    @InjectMocks
    private OperationDetailServiceImpl operationDetailService;

    @Mock
    private OperationDetailRepository operationDetailRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OperationRepository operationRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createOperationDetailSuccess() {
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setOperationId(1L);
        req.setSku("SKU123");
        req.setQuantity(10);
        req.setFromLocationName("Location A");
        req.setToLocationName("Location B");

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");
        product.setIsActive(true);

        Location fromLocation = new Location();
        fromLocation.setId(1L);
        fromLocation.setName("Location A");
        fromLocation.setIsActive(true);

        Location toLocation = new Location();
        toLocation.setId(2L);
        toLocation.setName("Location B");
        toLocation.setIsActive(true);

        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.CREATED);

        when(productRepository.findBySkuAndIsActiveTrue(req.getSku())).thenReturn(Optional.of(product));
        when(locationRepository.findByNameAndIsActiveTrue(req.getFromLocationName())).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findByNameAndIsActiveTrue(req.getToLocationName())).thenReturn(Optional.of(toLocation));
        when(operationRepository.findById(req.getOperationId())).thenReturn(Optional.of(operation));

        OperationDetail operationDetail = new OperationDetail();
        operationDetail.setQuantity(req.getQuantity());
        operationDetail.setProduct(product);
        operationDetail.setFromLocation(fromLocation);
        operationDetail.setToLocation(toLocation);
        operationDetail.setOperation(operation);

        when(operationDetailRepository.save(any(OperationDetail.class))).thenReturn(operationDetail);

        OperationDetailInfoResp response = operationDetailService.createOperationDetail(req);

        assertEquals(req.getOperationId(), response.getOperationId());
        assertEquals(req.getSku(), response.getSku());
        assertEquals(req.getQuantity(), response.getQuantity());
        assertEquals(req.getFromLocationName(), response.getFromLocationName());
        assertEquals(req.getToLocationName(), response.getToLocationName());
    }

    @Test
    void createOperationDetailProductNotFound() {
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setOperationId(1L);
        req.setSku("SKU123");
        req.setQuantity(10);
        req.setFromLocationName("Location A");
        req.setToLocationName("Location B");

        when(productRepository.findBySkuAndIsActiveTrue(req.getSku())).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.createOperationDetail(req));

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createOperationDetailFromLocationNotFound() {
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setOperationId(1L);
        req.setSku("SKU123");
        req.setQuantity(10);
        req.setFromLocationName("Location A");
        req.setToLocationName("Location B");

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");
        product.setIsActive(true);

        when(productRepository.findBySkuAndIsActiveTrue(req.getSku())).thenReturn(Optional.of(product));
        when(locationRepository.findByNameAndIsActiveTrue(req.getFromLocationName())).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.createOperationDetail(req));

        assertEquals("Departure location not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createOperationDetailToLocationNotFound() {

        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setOperationId(1L);
        req.setSku("SKU123");
        req.setQuantity(10);
        req.setFromLocationName("Location A");
        req.setToLocationName("Location B");

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");
        product.setIsActive(true);

        Location fromLocation = new Location();
        fromLocation.setId(1L);
        fromLocation.setName("Location A");
        fromLocation.setIsActive(true);

        when(productRepository.findBySkuAndIsActiveTrue(req.getSku())).thenReturn(Optional.of(product));
        when(locationRepository.findByNameAndIsActiveTrue(req.getFromLocationName())).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findByNameAndIsActiveTrue(req.getToLocationName())).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.createOperationDetail(req));
        assertEquals("Destination location not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createOperationDetailOperationNotFound() {
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setOperationId(1L);
        req.setSku("SKU123");
        req.setQuantity(10);
        req.setFromLocationName("Location A");
        req.setToLocationName("Location B");

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");
        product.setIsActive(true);

        Location fromLocation = new Location();
        fromLocation.setId(1L);
        fromLocation.setName("Location A");
        fromLocation.setIsActive(true);

        Location toLocation = new Location();
        toLocation.setId(2L);
        toLocation.setName("Location B");
        toLocation.setIsActive(true);

        when(productRepository.findBySkuAndIsActiveTrue(req.getSku())).thenReturn(Optional.of(product));
        when(locationRepository.findByNameAndIsActiveTrue(req.getFromLocationName())).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findByNameAndIsActiveTrue(req.getToLocationName())).thenReturn(Optional.of(toLocation));
        when(operationRepository.findById(req.getOperationId())).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.createOperationDetail(req));
        assertEquals("Operation not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createOperationDetailOperationStatusNotCreated() {
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setOperationId(1L);
        req.setSku("SKU123");
        req.setQuantity(10);
        req.setFromLocationName("Location A");
        req.setToLocationName("Location B");

            Product product = new Product();
            product.setId(1L);
            product.setSku("SKU123");
            product.setIsActive(true);

            Location fromLocation = new Location();
            fromLocation.setId(1L);
            fromLocation.setName("Location A");
            fromLocation.setIsActive(true);

            Location toLocation = new Location();
            toLocation.setId(2L);
            toLocation.setName("Location B");
            toLocation.setIsActive(true);

            Operation operation = new Operation();
            operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);

        when(productRepository.findBySkuAndIsActiveTrue(req.getSku())).thenReturn(Optional.of(product));
        when(locationRepository.findByNameAndIsActiveTrue(req.getFromLocationName())).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findByNameAndIsActiveTrue(req.getToLocationName())).thenReturn(Optional.of(toLocation));
        when(operationRepository.findById(req.getOperationId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () -> operationDetailService.createOperationDetail(req));

        assertEquals("Cannot add details to an operation that is not in 'CREATED' status", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getAllOperationDetailsWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "sku";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Long filter = 1L;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");

        Location fromLocation = new Location();
        fromLocation.setId(1L);
        fromLocation.setName("Location A");

        Location toLocation = new Location();
        toLocation.setId(2L);
        toLocation.setName("Location B");

        Operation operation = new Operation();
        operation.setId(1L);

        OperationDetail operationDetail = new OperationDetail();
        operationDetail.setOperation(operation);
        operationDetail.setProduct(product);
        operationDetail.setFromLocation(fromLocation);
        operationDetail.setToLocation(toLocation);
        OperationDetailInfoResp operationDetailInfoResp = new OperationDetailInfoResp();

        when(operationDetailRepository.findByOperationId(filter,pageable)).thenReturn(new PageImpl<>(List.of(operationDetail)));
        when(objectMapper.convertValue(operationDetail, OperationDetailInfoResp.class)).thenReturn(operationDetailInfoResp);

        Page<OperationDetailInfoResp> result = operationDetailService.getAllOperationDetails(pageNumber, pageSize, sortField, sortDirection, filter);

        assertEquals(1, result.getContent().size());
        assertEquals(operationDetailInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllOperationDetailsWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "sku";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");

        Location fromLocation = new Location();
        fromLocation.setId(1L);
        fromLocation.setName("Location A");

        Location toLocation = new Location();
        toLocation.setId(2L);
        toLocation.setName("Location B");

        Operation operation = new Operation();
        operation.setId(1L);

        OperationDetail operationDetail = new OperationDetail();
        operationDetail.setOperation(operation);
        operationDetail.setProduct(product);
        operationDetail.setFromLocation(fromLocation);
        operationDetail.setToLocation(toLocation);
        OperationDetailInfoResp operationDetailInfoResp = new OperationDetailInfoResp();

        when(operationDetailRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(operationDetail)));
        when(objectMapper.convertValue(operationDetail, OperationDetailInfoResp.class)).thenReturn(operationDetailInfoResp);

        Page<OperationDetailInfoResp> result = operationDetailService.getAllOperationDetails(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(operationDetailInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getDetailSuccess() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");
        product.setIsActive(true);

        Location fromLocation = new Location();
        fromLocation.setId(1L);
        fromLocation.setName("Location A");
        fromLocation.setIsActive(true);

        Location toLocation = new Location();
        toLocation.setId(2L);
        toLocation.setName("Location B");
        toLocation.setIsActive(true);

        Operation operation = new Operation();
        operation.setId(1L);

        OperationDetail operationDetail = new OperationDetail();
        operationDetail.setId(1L);
        operationDetail.setQuantity(10);
        operationDetail.setProduct(product);
        operationDetail.setFromLocation(fromLocation);
        operationDetail.setToLocation(toLocation);
        operationDetail.setOperation(operation);

        when(operationDetailRepository.findById(operationDetail.getId())).thenReturn(Optional.of(operationDetail));

        OperationDetailInfoResp response = operationDetailService.getDetail(operationDetail.getId());

        assertEquals(operationDetail.getId(), response.getId());
        assertEquals(operationDetail.getQuantity(), response.getQuantity());
        assertEquals(operationDetail.getProduct().getSku(), response.getSku());
        assertEquals(operationDetail.getFromLocation().getName(), response.getFromLocationName());
        assertEquals(operationDetail.getToLocation().getName(), response.getToLocationName());
        assertEquals(operationDetail.getOperation().getId(), response.getOperationId());
    }

    @Test
    void getDetailNotFound() {
        Long detailId = 999L;

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.getDetail(detailId));

        assertEquals("Operation detail not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateOperationDetailSuccess() {
        Long detailId = 1L;
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setSku("SKU123");
        req.setQuantity(10);
        req.setFromLocationName("Location A");
        req.setToLocationName("Location B");

        OperationDetail existingDetail = new OperationDetail();
        existingDetail.setId(detailId);

        Product product = new Product();
        product.setSku("SKU123");
        existingDetail.setProduct(product);

        Location fromLocation = new Location();
        fromLocation.setName("Location A");
        existingDetail.setFromLocation(fromLocation);

        Location toLocation = new Location();
        toLocation.setName("Location B");
        existingDetail.setToLocation(toLocation);

        Operation operation = new Operation();
        operation.setOperationStatus(OperationStatus.CREATED);
        existingDetail.setOperation(operation);
        OperationDetailInfoResp operationDetailInfoResp = new OperationDetailInfoResp();

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.of(existingDetail));
        when(productRepository.findBySkuAndIsActiveTrue("SKU123")).thenReturn(Optional.of(product));
        when(locationRepository.findByNameAndIsActiveTrue("Location A")).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findByNameAndIsActiveTrue("Location B")).thenReturn(Optional.of(toLocation));
        when(operationDetailRepository.save(existingDetail)).thenReturn(existingDetail);
        when(objectMapper.convertValue(existingDetail, OperationDetailInfoResp.class)).thenReturn(operationDetailInfoResp);

        OperationDetailInfoResp result = operationDetailService.updateOperationDetail(detailId, req);

        assertNotNull(result);
        assertEquals(existingDetail.getQuantity(), req.getQuantity());
        assertEquals(existingDetail.getProduct().getSku(), req.getSku());
        assertEquals(existingDetail.getFromLocation().getName(), req.getFromLocationName());
        assertEquals(existingDetail.getToLocation().getName(), req.getToLocationName());
    }

    @Test
    void updateOperationDetailNotFound() {
        Long detailId = 1L;
        OperationDetailInfoReq req = new OperationDetailInfoReq();

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.updateOperationDetail(detailId, req));
        assertEquals("OperationDetail with id: 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateOperationDetailOperationNotInCreatedStatus() {
        Long detailId = 1L;
        OperationDetailInfoReq req = new OperationDetailInfoReq();

        OperationDetail existingDetail = new OperationDetail();
        existingDetail.setId(detailId);

        Operation operation = new Operation();
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        existingDetail.setOperation(operation);

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.of(existingDetail));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.updateOperationDetail(detailId, req));
        assertEquals("Cannot update details to an operation that is not in 'CREATED' status", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void updateOperationDetailProductNotFound() {

        Long detailId = 1L;
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setSku("12345");

        OperationDetail existingDetail = new OperationDetail();
        existingDetail.setId(detailId);

        Operation operation = new Operation();
        operation.setOperationStatus(OperationStatus.CREATED);
        existingDetail.setOperation(operation);

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.of(existingDetail));
        when(productRepository.findBySkuAndIsActiveTrue(req.getSku())).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.updateOperationDetail(detailId, req));
        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateOperationDetailFromLocationNotFound() {

        Long detailId = 1L;
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setFromLocationName("Location A");

        OperationDetail existingDetail = new OperationDetail();
        existingDetail.setId(detailId);

        Operation operation = new Operation();
        operation.setOperationStatus(OperationStatus.CREATED);
        existingDetail.setOperation(operation);

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.of(existingDetail));

        when(locationRepository.findByNameAndIsActiveTrue("Location A")).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.updateOperationDetail(detailId, req));
        assertEquals("From location not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateOperationDetailToLocationNotFound() {

        Long detailId = 1L;
        OperationDetailInfoReq req = new OperationDetailInfoReq();
        req.setToLocationName("Location B");

        OperationDetail existingDetail = new OperationDetail();
        existingDetail.setId(detailId);

        Operation operation = new Operation();
        operation.setOperationStatus(OperationStatus.CREATED);
        existingDetail.setOperation(operation);

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.of(existingDetail));
        when(locationRepository.findByNameAndIsActiveTrue("Location B")).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.updateOperationDetail(detailId, req));
        assertEquals("Destination location not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

        @Test
    void deleteOperationDetailSuccess() {
        Long detailId = 1L;
        OperationDetail operationDetail = new OperationDetail();
        operationDetail.setId(detailId);

        Operation operation = new Operation();
        operation.setOperationStatus(OperationStatus.CREATED);
        operationDetail.setOperation(operation);

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.of(operationDetail));

        operationDetailService.deleteOperationDetail(detailId);

        verify(operationDetailRepository, times(1)).delete(operationDetail);
    }

    @Test
    void deleteOperationDetailOperationNotInCreatedStatus() {
        Long detailId = 1L;
        OperationDetail operationDetail = new OperationDetail();
        operationDetail.setId(detailId);

        Operation operation = new Operation();
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operationDetail.setOperation(operation);

        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.of(operationDetail));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.deleteOperationDetail(detailId));

        assertEquals("Cannot delete details from an operation that is not in 'CREATED' status", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void deleteOperationDetailNotFound() {
        Long detailId = 1L;
        when(operationDetailRepository.findById(detailId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationDetailService.deleteOperationDetail(detailId));

        assertEquals("Operation detail not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getOperationDetailInfoResp() {
        OperationDetail detail = new OperationDetail();
        detail.setId(1L);

        Operation operation = new Operation();
        operation.setId(2L);
        detail.setOperation(operation);

        Location fromLocation = new Location();
        fromLocation.setName("Location A");
        detail.setFromLocation(fromLocation);

        Location toLocation = new Location();
        toLocation.setName("Location B");
        detail.setToLocation(toLocation);

        Product product = new Product();
        product.setSku("SKU123");
        detail.setProduct(product);

        OperationDetailInfoResp expectedResp = new OperationDetailInfoResp();
        expectedResp.setOperationId(2L);
        expectedResp.setFromLocationName("Location A");
        expectedResp.setToLocationName("Location B");
        expectedResp.setSku("SKU123");

        when(objectMapper.convertValue(detail, OperationDetailInfoResp.class)).thenReturn(expectedResp);

        OperationDetailInfoResp result = operationDetailService.getOperationDetailInfoResp(detail);

        assertNotNull(result);
        assertEquals(expectedResp.getOperationId(), result.getOperationId());
        assertEquals(expectedResp.getFromLocationName(), result.getFromLocationName());
        assertEquals(expectedResp.getToLocationName(), result.getToLocationName());
        assertEquals(expectedResp.getSku(), result.getSku());
    }
}