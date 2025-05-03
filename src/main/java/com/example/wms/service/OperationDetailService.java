package com.example.wms.service;

import com.example.wms.model.db.entity.OperationDetail;
import com.example.wms.model.dto.request.OperationDetailInfoReq;
import com.example.wms.model.dto.response.OperationDetailInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface OperationDetailService {

    @Transactional
    OperationDetailInfoResp createOperationDetail(OperationDetailInfoReq req);

    @Transactional
    OperationDetailInfoResp getDetail(Long detailId);

    @Transactional
    void deleteOperationDetail(Long detailId);

    @Transactional
    OperationDetailInfoResp updateOperationDetail(Long id, OperationDetailInfoReq req);

    Page<OperationDetailInfoResp> getAllOperationDetails(Integer page, Integer perPage, String sort, Sort.Direction order, Long filter);

    OperationDetailInfoResp getOperationDetailInfoResp(OperationDetail detail);
}
