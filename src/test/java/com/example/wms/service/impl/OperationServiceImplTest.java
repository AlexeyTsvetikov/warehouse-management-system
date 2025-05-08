package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.*;
import com.example.wms.model.db.repository.DocumentRepository;
import com.example.wms.model.db.repository.OperationRepository;
import com.example.wms.model.db.repository.UserRepository;
import com.example.wms.model.dto.request.OperationInfoReq;
import com.example.wms.model.dto.response.OperationInfoResp;
import com.example.wms.model.enums.OperationStatus;
import com.example.wms.model.enums.OperationType;
import com.example.wms.service.StockService;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OperationServiceImplTest {

    @InjectMocks
    private OperationServiceImpl operationService;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private StockService stockService;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createOperationSuccess() {
        OperationInfoReq req = new OperationInfoReq();
        req.setUserId(1L);
        req.setDocumentId(2L);
        req.setOperationType(OperationType.RECEIVING);

        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        Document document = new Document();
        document.setId(2L);
        document.setIsActive(true);

        when(userRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(document));

        Operation savedOperation = new Operation();
        savedOperation.setId(3L);
        savedOperation.setDocument(document);
        savedOperation.setUser(user);

        when(operationRepository.save(any(Operation.class))).thenReturn(savedOperation);

        OperationInfoResp response = operationService.createOperation(req);

        assertEquals(savedOperation.getOperationType(), response.getOperationType());
        assertEquals(savedOperation.getDocument().getNumber(), response.getDocumentNumber());
        assertEquals(savedOperation.getUser().getUsername(), response.getUsername());
    }

    @Test
    void createOperationUserNotFound() {
        OperationInfoReq req = new OperationInfoReq();
        req.setUserId(1L);
        req.setDocumentId(2L);

        when(userRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.createOperation(req));
        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createOperationDocumentNotFound() {
        OperationInfoReq req = new OperationInfoReq();
        req.setUserId(1L);
        req.setDocumentId(2L);

        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        when(userRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.createOperation(req));

        assertEquals("Document not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void startOperationSuccess() {
        Long operationId = 1L;
        Operation operation = new Operation();
        operation.setId(operationId);
        operation.setOperationStatus(OperationStatus.CREATED);

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        operationService.startOperation(operationId);

        assertEquals(OperationStatus.IN_PROGRESS, operation.getOperationStatus());
        verify(operationRepository).save(operation);
    }

    @Test
    void startOperationOperationNotFound() {
        Long operationId = 1L;
        String expectedErrorMessage = String.format("Operation with id: %s not found", operationId);

        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.startOperation(operationId));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void startOperationOperationNotInCreatedStatus() {
        Long operationId = 1L;
        Operation operation = new Operation();
        operation.setId(operationId);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.startOperation(operationId));

        assertEquals("Cannot start an operation that is not in 'CREATED' status", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void receivingOperationSuccess() {
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        Document document = new Document();
        document.setId(2L);
        document.setIsActive(true);


        Operation operation = new Operation();
        operation.setId(1L);
        operation.setUser(user);
        operation.setDocument(document);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);

        Product product = new Product();
        product.setId(1L);

        Location location = new Location();
        location.setId(1L);

        OperationDetail detail = new OperationDetail();
        detail.setProduct(product);
        detail.setQuantity(10);
        detail.setToLocation(location);
        operation.setOperationDetails(List.of(detail));

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        OperationInfoResp response = operationService.receivingOperation(operation.getId());

        assertEquals(OperationStatus.COMPLETED, response.getOperationStatus());
        verify(stockService).increaseStock(detail.getProduct(), detail.getToLocation(), detail.getQuantity());
        verify(operationRepository).save(operation);
    }

    @Test
    void receivingOperationOperationNotFound() {
        Long operationId = 1L;
        String expectedErrorMessage = String.format("Operation  with id: %s not found", operationId);

        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.receivingOperation(operationId));
        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void receivingOperationOperationNotInProgress() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.CREATED);

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.receivingOperation(operation.getId()));
        assertEquals("The operation must be in the 'IN_PROGRESS' status", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void receivingOperationNoDetails() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operation.setOperationDetails(Collections.emptyList());

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.receivingOperation(operation.getId()));
        assertEquals("There are no details for the operation.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void receivingOperationErrorDuringStockIncrease() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);

        Product product = new Product();
        product.setId(1L);

        Location location = new Location();
        location.setId(1L);

        OperationDetail detail = new OperationDetail();
        detail.setProduct(product);
        detail.setQuantity(10);
        detail.setToLocation(location);
        operation.setOperationDetails(List.of(detail));

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));
        doThrow(new CommonBackendException("Stock error", HttpStatus.INTERNAL_SERVER_ERROR))
                .when(stockService).increaseStock(any(), any(), any());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.receivingOperation(operation.getId()));
        assertEquals("Error during receiving for operation ID: " + operation.getId() + ": Stock error", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        verify(operationRepository, never()).save(any(Operation.class));
    }

    @Test
    void receivingOperationNullDetails() {
        Long operationId = 1L;
        Operation operation = new Operation();
        operation.setId(operationId);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operation.setOperationDetails(null);

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.receivingOperation(operationId));
        assertEquals("There are no details for the operation.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void shippingOperationSuccess() {
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        Document document = new Document();
        document.setId(2L);
        document.setIsActive(true);

        Product product = new Product();
        product.setId(1L);

        Location location = new Location();
        location.setId(1L);

        Long operationId = 1L;
        Operation operation = new Operation();
        operation.setId(operationId);
        operation.setDocument(document);
        operation.setUser(user);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);

        OperationDetail detail = new OperationDetail();
        detail.setProduct(product);
        detail.setQuantity(10);
        detail.setFromLocation(location);
        operation.setOperationDetails(List.of(detail));

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        OperationInfoResp response = operationService.shippingOperation(operationId);

        assertEquals(OperationStatus.COMPLETED, response.getOperationStatus());
        verify(stockService).decreaseStock(detail.getProduct(), detail.getFromLocation(), detail.getQuantity());
        verify(operationRepository).save(operation);
    }

    @Test
    void shippingOperationOperationNotFound() {
        Long operationId = 1L;
        String expectedErrorMessage = String.format("Operation  with id: %s not found", operationId);

        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.shippingOperation(operationId));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void shippingOperationOperationNotInProgress() {

        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.CREATED);

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.shippingOperation(operation.getId()));

        assertEquals("The operation must be in the 'IN_PROGRESS' status", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void shippingOperationNoDetails() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operation.setOperationDetails(Collections.emptyList());

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.shippingOperation(operation.getId()));

        assertEquals("There are no details for the operation.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void shippingOperationNullDetails() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operation.setOperationDetails(null);

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.shippingOperation(operation.getId()));

        assertEquals("There are no details for the operation.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void shippingOperationErrorDuringStockDecrease() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);

        Product product = new Product();
        product.setId(1L);

        Location location = new Location();
        location.setId(1L);

        OperationDetail detail = new OperationDetail();
        detail.setProduct(product);
        detail.setQuantity(10);
        detail.setFromLocation(location);
        operation.setOperationDetails(List.of(detail));

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));
        doThrow(new CommonBackendException("Stock error", HttpStatus.INTERNAL_SERVER_ERROR))
                .when(stockService).decreaseStock(any(), any(), any());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.shippingOperation(operation.getId()));
        assertEquals("Error during shipping for operation ID: " + operation.getId() + ": Stock error", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        verify(operationRepository, never()).save(any(Operation.class));
    }

    @Test
    void transferOperationSuccess() {
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        Document document = new Document();
        document.setId(2L);
        document.setIsActive(true);

        Product product = new Product();
        product.setId(1L);

        Location location1 = new Location();
        location1.setId(1L);

        Location location2 = new Location();
        location2.setId(2L);

        Long operationId = 1L;
        Operation operation = new Operation();
        operation.setId(operationId);
        operation.setUser(user);
        operation.setDocument(document);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);

        OperationDetail detail = new OperationDetail();
        detail.setProduct(product);
        detail.setQuantity(10);
        detail.setFromLocation(location1);
        detail.setToLocation(location2);
        operation.setOperationDetails(List.of(detail));

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        OperationInfoResp response = operationService.transferOperation(operationId);

        assertEquals(OperationStatus.COMPLETED, response.getOperationStatus());
        verify(stockService).transferStock(detail.getProduct(), detail.getQuantity(), detail.getFromLocation(), detail.getToLocation());
        verify(operationRepository).save(operation);
    }

    @Test
    void transferOperationOperationNotFound() {
        Long operationId = 1L;
        String expectedErrorMessage = String.format("Operation with id: %s not found", operationId);

        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.transferOperation(operationId));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void transferOperationOperationNotInProgress() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.CREATED);

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.transferOperation(operation.getId()));

        assertEquals("The operation must be in the 'IN_PROGRESS' status", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void transferOperationNoDetails() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operation.setOperationDetails(Collections.emptyList());

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.transferOperation(operation.getId()));
        assertEquals("There are no details for the operation.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void transferOperationNullDetails() {
        Long operationId = 1L;
        Operation operation = new Operation();
        operation.setId(operationId);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operation.setOperationDetails(null);

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.transferOperation(operationId));
        assertEquals("There are no details for the operation.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void transferOperationErrorDuringStockTransfer() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.IN_PROGRESS);

        Product product = new Product();
        product.setId(1L);

        Location location1 = new Location();
        location1.setId(1L);

        Location location2 = new Location();
        location2.setId(1L);

        OperationDetail detail = new OperationDetail();
        detail.setProduct(product);
        detail.setQuantity(10);
        detail.setFromLocation(location1);
        detail.setToLocation(location2);
        operation.setOperationDetails(List.of(detail));

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));
        doThrow(new CommonBackendException("Stock transfer error", HttpStatus.INTERNAL_SERVER_ERROR))
                .when(stockService).transferStock(any(), any(), any(), any());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.transferOperation(operation.getId()));
        assertEquals("Error during stock transfer for operation ID: " + operation.getId() + ": Stock transfer error", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        verify(operationRepository, never()).save(any(Operation.class));
    }

    @Test
    void getOperationSuccess() {
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);
        user.setUsername("TestName");

        Document document = new Document();
        document.setId(2L);
        document.setIsActive(true);
        document.setNumber("TestNumber");

        Operation operation = new Operation();
        operation.setId(1L);
        operation.setDocument(document);
        operation.setUser(user);

        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        OperationInfoResp response = operationService.getOperation(operation.getId());

        assertEquals(operation.getId(), response.getId());
        assertEquals(operation.getUser().getUsername(), response.getUsername());
        assertEquals(operation.getDocument().getNumber(), response.getDocumentNumber());
    }

    @Test
    void getOperationOperationNotFound() {
        Long operationId = 1L;
        String expectedErrorMessage = String.format("Operation with id: %s not found", operationId);

        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.getOperation(operationId));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getAllOperationsWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "date";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        OperationType filter = OperationType.RECEIVING;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        Document document = new Document();
        document.setId(2L);
        document.setIsActive(true);

        Operation operation = new Operation();
        operation.setUser(user);
        operation.setDocument(document);
        OperationInfoResp operationInfoResp = new OperationInfoResp();

        when(operationRepository.findAllFiltered(filter, pageable)).thenReturn(new PageImpl<>(List.of(operation)));
        when(objectMapper.convertValue(operation, OperationInfoResp.class)).thenReturn(operationInfoResp);

        Page<OperationInfoResp> result = operationService.getAllOperations(pageNumber, pageSize, sortField, sortDirection, filter);

        assertEquals(1, result.getContent().size());
        assertEquals(operationInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllOperationsWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "date";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        Document document = new Document();
        document.setId(2L);
        document.setIsActive(true);

        Operation operation = new Operation();
        operation.setUser(user);
        operation.setDocument(document);
        OperationInfoResp operationInfoResp = new OperationInfoResp();

        when(operationRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(operation)));
        when(objectMapper.convertValue(operation, OperationInfoResp.class)).thenReturn(operationInfoResp);

        Page<OperationInfoResp> result = operationService.getAllOperations(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(operationInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }


    @Test
    void cancelOperationOperationNotFound() {
        Long operationId = 1L;
        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.cancelOperation(operationId));
        assertEquals(String.format("Operation with id: %s not found", operationId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void cancelOperationOperationCompleted() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.COMPLETED);
        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.cancelOperation(operation.getId()));
        assertEquals("Cannot cancel a completed operation", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void cancelOperationOperationCancelled() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.CANCELLED);
        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                operationService.cancelOperation(operation.getId()));

        assertEquals("Operation is already cancelled", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void cancelOperationSuccess() {
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setOperationStatus(OperationStatus.CREATED);
        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));

        operationService.cancelOperation(operation.getId());

        assertEquals(OperationStatus.CANCELLED, operation.getOperationStatus());
        verify(operationRepository).save(operation);
    }
}