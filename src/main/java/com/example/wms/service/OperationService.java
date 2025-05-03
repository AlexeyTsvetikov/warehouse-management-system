package com.example.wms.service;


import com.example.wms.model.dto.request.OperationInfoReq;
import com.example.wms.model.dto.response.OperationInfoResp;
import com.example.wms.model.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public interface OperationService {

    @Transactional
    OperationInfoResp createOperation(OperationInfoReq req);

    @Transactional
    void startOperation(Long id);

    @Transactional
    OperationInfoResp receivingOperation(Long id);

    @Transactional
    OperationInfoResp shippingOperation(Long id);

    @Transactional
    OperationInfoResp transferOperation(Long id);

    @Transactional(readOnly = true)
    OperationInfoResp getOperation(Long id);

    @Transactional(readOnly = true)
    Page<OperationInfoResp> getAllOperations(Integer page, Integer perPage, String sort, Sort.Direction order,  OperationType filter);

    @Transactional
    void cancelOperation(Long id);
}
