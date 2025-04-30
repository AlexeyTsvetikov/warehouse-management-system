package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Partner;
import com.example.wms.model.db.repository.PartnerRepository;
import com.example.wms.model.dto.request.PartnerInfoReq;
import com.example.wms.model.dto.response.PartnerInfoResp;
import com.example.wms.service.PartnerService;
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
public class PartnerServiceImpl implements PartnerService {
    private final PartnerRepository partnerRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PartnerInfoResp createPartner(PartnerInfoReq req) {
        if (partnerRepository.findByName(req.getName()).isPresent()) {
            throw new CommonBackendException("Partner with name already exists", HttpStatus.CONFLICT);
        }

        Partner partner = objectMapper.convertValue(req, Partner.class);
        partner.setIsActive(true);

        Partner savedPartner = partnerRepository.save(partner);

        return objectMapper.convertValue(savedPartner, PartnerInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerInfoResp getPartner(Long id) {
        final String errMsg = String.format("Partner with id: %s not found", id);

        Partner partner = partnerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        return objectMapper.convertValue(partner, PartnerInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartnerInfoResp> getAllPartners(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Partner> partners;

        partners = partnerRepository.findAllByIsActiveTrue(pageRequest);

        List<PartnerInfoResp> content = partners.getContent().stream()
                .map(partner -> objectMapper.convertValue(partner, PartnerInfoResp.class))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, partners.getTotalElements());
    }

    @Override
    @Transactional
    public PartnerInfoResp updatePartner(Long id, PartnerInfoReq req) {
        final String errMsg = String.format("Partner with id: %s not found", id);

        Partner partner = partnerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        partner.setName(req.getName() != null ? req.getName() : partner.getName());
        partner.setPartnerType(req.getPartnerType() != null ? req.getPartnerType() : partner.getPartnerType());
        partner.setAddress(req.getAddress() != null ? req.getAddress() : partner.getAddress());
        partner.setEmail(req.getEmail() != null ? req.getEmail() : partner.getEmail());
        partner.setPhone(req.getPhone() != null ? req.getPhone() : partner.getPhone());

        Partner updatedPartner = partnerRepository.save(partner);
        return objectMapper.convertValue(updatedPartner, PartnerInfoResp.class);
    }

    @Override
    @Transactional
    public void deletePartner(Long id) {
        final String errMsg = String.format("Partner with id: %s not found", id);

        Partner partner = partnerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        partner.setIsActive(false);
        partnerRepository.save(partner);
    }


}
