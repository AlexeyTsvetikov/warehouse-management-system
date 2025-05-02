package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.*;
import com.example.wms.model.db.repository.*;
import com.example.wms.model.dto.request.OperationDetailInfoReq;
import com.example.wms.model.dto.response.OperationDetailInfoResp;
import com.example.wms.model.enums.OperationStatus;
import com.example.wms.service.OperationDetailService;
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
public class OperationDetailServiceImpl implements OperationDetailService {
    private final OperationDetailRepository operationDetailRepository;
    private final ObjectMapper objectMapper;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final OperationRepository operationRepository;

    @Override
    @Transactional
    public OperationDetailInfoResp createOperationDetail(OperationDetailInfoReq req) {
        Product product = productRepository.findBySkuAndIsActiveTrue(req.getSku())
                .orElseThrow(() -> new CommonBackendException("Product not found", HttpStatus.NOT_FOUND));

        Location fromLocation = locationRepository.findByNameAndIsActiveTrue(req.getFromLocationName())
                .orElseThrow(() -> new CommonBackendException("Departure location not found", HttpStatus.NOT_FOUND));

        Location toLocation = locationRepository.findByNameAndIsActiveTrue(req.getToLocationName())
                .orElseThrow(() -> new CommonBackendException("Destination location not found", HttpStatus.NOT_FOUND));

        Operation operation = operationRepository.findById(req.getOperationId())
                .orElseThrow(() -> new CommonBackendException("Operation not found", HttpStatus.NOT_FOUND));

        if (operation.getOperationStatus() != OperationStatus.CREATED) {
            throw new CommonBackendException("Cannot add details to an operation that is not in 'CREATED' status", HttpStatus.FORBIDDEN);
        }

        OperationDetail operationDetail = objectMapper.convertValue(req, OperationDetail.class);
        operationDetail.setProduct(product);
        operationDetail.setFromLocation(fromLocation);
        operationDetail.setToLocation(toLocation);
        operationDetail.setOperation(operation);

        OperationDetail savedOperationDetail = operationDetailRepository.save(operationDetail);

        return getOperationDetailInfoResp(savedOperationDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperationDetailInfoResp> getAllOperationDetails(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<OperationDetail> operationDetails = operationDetailRepository.findAll(pageRequest);

        List<OperationDetailInfoResp> content = operationDetails.getContent().stream()
                .map(this::getOperationDetailInfoResp).collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, operationDetails.getTotalElements());
    }


    @Override
    @Transactional(readOnly = true)
    public OperationDetailInfoResp getDetail(Long detailId) {
        OperationDetail detail = operationDetailRepository.findById(detailId)
                .orElseThrow(() -> new CommonBackendException("Operation detail not found", HttpStatus.NOT_FOUND));

        return getOperationDetailInfoResp(detail);
    }


    @Override
    @Transactional
    public OperationDetailInfoResp updateOperationDetail(Long id, OperationDetailInfoReq req) {
        final String errMsg = String.format("OperationDetail with id: %s not found", id);

        OperationDetail operationDetail = operationDetailRepository.findById(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        if (operationDetail.getOperation().getOperationStatus() != OperationStatus.CREATED) {
            throw new CommonBackendException(
                    "Cannot update details to an operation that is not in 'CREATED' status",
                    HttpStatus.FORBIDDEN);
        }

        if (req.getSku() != null) {
            Product product = productRepository.findBySkuAndIsActiveTrue(req.getSku())
                    .orElseThrow(() -> new CommonBackendException("Product not found", HttpStatus.NOT_FOUND));
            operationDetail.setProduct(product);
        }

        if (req.getQuantity() != null) {
            operationDetail.setQuantity(req.getQuantity());
        }

        if (req.getFromLocationName() != null) {
            Location fromLocation = locationRepository.findByNameAndIsActiveTrue(req.getFromLocationName())
                    .orElseThrow(() -> new CommonBackendException("Departure location not found", HttpStatus.NOT_FOUND));
            operationDetail.setFromLocation(fromLocation);
        }

        if (req.getToLocationName() != null) {
            Location toLocation = locationRepository.findByNameAndIsActiveTrue(req.getToLocationName())
                    .orElseThrow(() -> new CommonBackendException("Destination location not found", HttpStatus.NOT_FOUND));
            operationDetail.setToLocation(toLocation);
        }

        OperationDetail updatedOperationDetail = operationDetailRepository.save(operationDetail);
        return getOperationDetailInfoResp(updatedOperationDetail);
    }

    @Override
    @Transactional
    public void deleteOperationDetail(Long detailId) {
        OperationDetail detail = operationDetailRepository.findById(detailId)
                .orElseThrow(() -> new CommonBackendException("Operation detail not found", HttpStatus.NOT_FOUND));

        Operation operation = detail.getOperation();
        if (operation.getOperationStatus() != OperationStatus.CREATED) {
            throw new CommonBackendException("Cannot delete details from an operation that is not in 'CREATED' status", HttpStatus.FORBIDDEN);
        }

        operationDetailRepository.delete(detail);
    }

    @Override
    public OperationDetailInfoResp getOperationDetailInfoResp(OperationDetail detail) {
        OperationDetailInfoResp resp = objectMapper.convertValue(detail, OperationDetailInfoResp.class);
        resp.setOperationId(detail.getOperation().getId());
        resp.setFromLocationName(detail.getFromLocation().getName());
        resp.setToLocationName(detail.getToLocation().getName());
        resp.setSku(detail.getProduct().getSku());
        return resp;
    }

}

