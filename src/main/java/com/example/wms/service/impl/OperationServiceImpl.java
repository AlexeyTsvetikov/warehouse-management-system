package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.*;
import com.example.wms.model.db.repository.*;
import com.example.wms.model.dto.request.OperationInfoReq;
import com.example.wms.model.dto.response.OperationInfoResp;
import com.example.wms.model.enums.OperationStatus;
import com.example.wms.model.enums.OperationType;
import com.example.wms.service.OperationService;
import com.example.wms.service.StockService;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final StockService stockService;

    @Override
    @Transactional
    public OperationInfoResp createOperation(OperationInfoReq req) {
        User user = userRepository.findByIdAndIsActiveTrue(req.getUserId())
                .orElseThrow(() -> new CommonBackendException("User not found", HttpStatus.NOT_FOUND));

        Document document = documentRepository.findByIdAndIsActiveTrue(req.getDocumentId())
                .orElseThrow(() -> new CommonBackendException("Document not found", HttpStatus.NOT_FOUND));

        Operation operation = new Operation();
        operation.setOperationType(req.getOperationType());
        operation.setUser(user);
        operation.setDocument(document);
        operation.setOperationStatus(OperationStatus.CREATED);
        Operation savedOperation = operationRepository.save(operation);

        return getOperationInfoResp(savedOperation);

    }

    @Override
    @Transactional
    public void startOperation(Long id) {
        final String errMsg = String.format("Operation with id: %s not found", id);

        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        if (operation.getOperationStatus() != OperationStatus.CREATED) {
            throw new CommonBackendException("Cannot start an operation that is not in 'CREATED' status", HttpStatus.FORBIDDEN);
        }

        operation.setOperationStatus(OperationStatus.IN_PROGRESS);
        operationRepository.save(operation);
    }

    @Override
    @Transactional
    public OperationInfoResp receivingOperation(Long id) {
        final String errMsg = String.format("Operation  with id: %s not found", id);

        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        if (operation.getOperationStatus() != OperationStatus.IN_PROGRESS) {
            throw new CommonBackendException("The operation must be in the 'IN_PROGRESS' status", HttpStatus.FORBIDDEN);
        }
        log.info("Starting receiving for operation ID: {}", id);

        List<OperationDetail> details = operation.getOperationDetails();

        if (details == null || details.isEmpty()) {
            throw new CommonBackendException("There are no details for the operation.", HttpStatus.BAD_REQUEST);
        }

        try {
            for (OperationDetail detail : details) {
                Product product = detail.getProduct();
                Integer quantity = detail.getQuantity();
                Location toLocation = detail.getToLocation();
                stockService.increaseStock(product, toLocation, quantity);
            }
            operation.setOperationStatus(OperationStatus.COMPLETED);
            operationRepository.save(operation);
        } catch (CommonBackendException e) {
            throw new CommonBackendException("Error during receiving for operation ID: " + id + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return getOperationInfoResp(operation);
    }

    @Override
    @Transactional
    public OperationInfoResp shippingOperation(Long id) {
        final String errMsg = String.format("Operation  with id: %s not found", id);

        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        if (operation.getOperationStatus() != OperationStatus.IN_PROGRESS) {
            throw new CommonBackendException("The operation must be in the 'IN_PROGRESS' status", HttpStatus.FORBIDDEN);
        }
        log.info("Starting shipping for operation ID: {}", id);

        List<OperationDetail> details = operation.getOperationDetails();

        if (details == null || details.isEmpty()) {
            throw new CommonBackendException("There are no details for the operation.", HttpStatus.BAD_REQUEST);
        }

        try {
            for (OperationDetail detail : details) {
                Product product = detail.getProduct();
                Integer quantity = detail.getQuantity();
                Location fromLocation = detail.getFromLocation();
                stockService.decreaseStock(product, fromLocation, quantity);
            }
            operation.setOperationStatus(OperationStatus.COMPLETED);
            operationRepository.save(operation);
        } catch (CommonBackendException e) {
            throw new CommonBackendException("Error during shipping for operation ID: " + id + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return getOperationInfoResp(operation);
    }

    @Override
    @Transactional
    public OperationInfoResp transferOperation(Long id) {
        final String errMsg = String.format("Operation with id: %s not found", id);

        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        if (operation.getOperationStatus() != OperationStatus.IN_PROGRESS) {
            throw new CommonBackendException("The operation must be in the 'IN_PROGRESS' status", HttpStatus.FORBIDDEN);
        }

        log.info("Starting transfer for operation ID: {}", id);

        List<OperationDetail> details = operation.getOperationDetails();

        if (details == null || details.isEmpty()) {
            throw new CommonBackendException("There are no details for the operation.", HttpStatus.BAD_REQUEST);
        }

        try {
            for (OperationDetail detail : details) {
                Product product = detail.getProduct();
                Integer quantity = detail.getQuantity();
                Location fromLocation = detail.getFromLocation();
                Location toLocation = detail.getToLocation();

                stockService.transferStock(product, quantity, fromLocation, toLocation);
            }
            operation.setOperationStatus(OperationStatus.COMPLETED);
            operationRepository.save(operation);
        } catch (CommonBackendException e) {
            throw new CommonBackendException("Error during stock transfer for operation ID: " + id + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return getOperationInfoResp(operation);
    }

    @Override
    @Transactional(readOnly = true)
    public OperationInfoResp getOperation(Long id) {
        final String errMsg = String.format("Operation with id: %s not found", id);

        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        return getOperationInfoResp(operation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperationInfoResp> getAllOperations(Integer page, Integer perPage, String sort, Sort.Direction order, OperationType filter) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Operation> operations;

        if (filter != null && StringUtils.hasText(String.valueOf(filter))) {
            operations = operationRepository.findAllFiltered(filter, pageRequest);
        } else {
            operations = operationRepository.findAll(pageRequest);
        }

        List<OperationInfoResp> content = operations.getContent().stream()
                .map(this::getOperationInfoResp).collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, operations.getTotalElements());
    }

    @Override
    @Transactional
    public void cancelOperation(Long id) {
        final String errMsg = String.format("Operation with id: %s not found", id);

        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        if (operation.getOperationStatus() == OperationStatus.COMPLETED) {
            throw new CommonBackendException("Cannot cancel a completed operation", HttpStatus.FORBIDDEN);
        }

        if (operation.getOperationStatus() == OperationStatus.CANCELLED) {
            throw new CommonBackendException("Operation is already cancelled", HttpStatus.FORBIDDEN);
        }

        operation.setOperationStatus(OperationStatus.CANCELLED);
        operationRepository.save(operation);
    }

    private OperationInfoResp getOperationInfoResp(Operation operation) {

        OperationInfoResp resp = objectMapper.convertValue(operation, OperationInfoResp.class);
        resp.setDocumentNumber(operation.getDocument().getNumber());
        resp.setUsername(operation.getUser().getUsername());
        return resp;
    }

}

