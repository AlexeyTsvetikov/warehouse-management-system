package com.example.wms.service;

import com.example.wms.model.dto.request.PartnerInfoReq;
import com.example.wms.model.dto.response.PartnerInfoResp;
import com.example.wms.model.enums.PartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface PartnerService {
    @Transactional
    PartnerInfoResp createPartner(PartnerInfoReq req);

    @Transactional(readOnly = true)
    PartnerInfoResp getPartner(Long id);

    @Transactional(readOnly = true)
    Page<PartnerInfoResp> getAllPartners(Integer page, Integer perPage, String sort, Sort.Direction order, PartnerType filter);

    @Transactional
    PartnerInfoResp updatePartner(Long id, PartnerInfoReq req);

    @Transactional
    void deletePartner(Long id);
}
