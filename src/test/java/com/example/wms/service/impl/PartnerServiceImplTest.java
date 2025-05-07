package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Partner;
import com.example.wms.model.db.repository.PartnerRepository;
import com.example.wms.model.dto.request.PartnerInfoReq;
import com.example.wms.model.dto.response.PartnerInfoResp;
import com.example.wms.model.enums.PartnerType;
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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PartnerServiceImplTest {

    @InjectMocks
    private PartnerServiceImpl partnerService;

    @Mock
    private PartnerRepository partnerRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createPartner() {
        PartnerInfoReq req = new PartnerInfoReq();
        req.setName("TestName");
        req.setPartnerType(PartnerType.SUPPLIER);
        req.setAddress("TestAddress");
        req.setEmail("TestEmail");
        req.setPhone("TestPhone");

        Partner partner = new Partner();
        partner.setPartnerType(req.getPartnerType());
        partner.setEmail(req.getEmail());
        partner.setAddress(req.getAddress());
        partner.setName(req.getName());
        partner.setPhone(req.getPhone());

        when(partnerRepository.findByName(req.getName())).thenReturn(Optional.empty());

        when(partnerRepository.save(any(Partner.class))).thenReturn(partner);

        PartnerInfoResp partnerInfoResp = partnerService.createPartner(req);

        assertEquals(partner.getName(), partnerInfoResp.getName());
        assertEquals(partner.getPartnerType(), partnerInfoResp.getPartnerType());
        assertEquals(partner.getAddress(), partnerInfoResp.getAddress());
        assertEquals(partner.getEmail(), partnerInfoResp.getEmail());
        assertEquals(partner.getPhone(), partnerInfoResp.getPhone());
    }

    @Test
    void createPartnerExists() {
        PartnerInfoReq req = new PartnerInfoReq();
        req.setName("ExistingPartner");

        Partner existingPartner = new Partner();
        existingPartner.setName("ExistingPartner");

        when(partnerRepository.findByName(req.getName())).thenReturn(Optional.of(existingPartner));

        assertThrows(CommonBackendException.class, () -> partnerService.createPartner(req));
    }

    @Test
    void getPartner() {
        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("TestName");
        partner.setPartnerType(PartnerType.SUPPLIER);
        partner.setAddress("TestAddress");
        partner.setEmail("TestEmail");
        partner.setPhone("TestPhone");

        when(partnerRepository.findByIdAndIsActiveTrue(partner.getId())).thenReturn(Optional.of(partner));

        PartnerInfoResp partnerInfoResp = partnerService.getPartner(partner.getId());
        assertEquals(partner.getId(), partnerInfoResp.getId());
        assertEquals(partner.getName(), partnerInfoResp.getName());
        assertEquals(partner.getPartnerType(), partnerInfoResp.getPartnerType());
        assertEquals(partner.getAddress(), partnerInfoResp.getAddress());
        assertEquals(partner.getEmail(), partnerInfoResp.getEmail());
        assertEquals(partner.getPhone(), partnerInfoResp.getPhone());
    }

    @Test
    void getPartnerNotFound() {
        Long nonExistingId = 999L;
        assertPartnerNotFound(nonExistingId);
    }

    @Test
    void getAllPartnersWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String filter = "SUPPLIER";

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Partner partner = new Partner();
        PartnerInfoResp partnerInfoResp = new PartnerInfoResp();

        when(partnerRepository.findAllFiltered(PartnerType.valueOf(filter), pageable)).thenReturn(new PageImpl<>(List.of(partner)));
        when(objectMapper.convertValue(partner, PartnerInfoResp.class)).thenReturn(partnerInfoResp);

        Page<PartnerInfoResp> result = partnerService.getAllPartners(pageNumber, pageSize, sortField, sortDirection, PartnerType.valueOf(filter));

        assertEquals(1, result.getContent().size());
        assertEquals(partnerInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllPartnersWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Partner partner = new Partner();
        PartnerInfoResp partnerInfoResp = new PartnerInfoResp();

        when(partnerRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(partner)));
        when(objectMapper.convertValue(partner, PartnerInfoResp.class)).thenReturn(partnerInfoResp);

        Page<PartnerInfoResp> result = partnerService.getAllPartners(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(partnerInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updatePartnerAllValues() {
        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("TestName");
        partner.setPartnerType(PartnerType.SUPPLIER);
        partner.setAddress("TestAddress");
        partner.setEmail("TestEmail");
        partner.setPhone("TestPhone");

        PartnerInfoReq req = new PartnerInfoReq();
        req.setName("NewName");
        req.setPartnerType(PartnerType.CUSTOMER);
        req.setAddress("NewAddress");
        req.setEmail("NewEmail");
        req.setPhone("NewPhone");

        when(partnerRepository.findByIdAndIsActiveTrue(partner.getId())).thenReturn(Optional.of(partner));

        when(partnerRepository.save(any(Partner.class))).thenReturn(partner);

        PartnerInfoResp expectedResponse = new PartnerInfoResp();
        expectedResponse.setId(partner.getId());
        expectedResponse.setName(req.getName());
        expectedResponse.setPartnerType(req.getPartnerType());
        expectedResponse.setAddress(req.getAddress());
        expectedResponse.setEmail(req.getEmail());
        expectedResponse.setPhone(req.getPhone());
        when(objectMapper.convertValue(partner, PartnerInfoResp.class)).thenReturn(expectedResponse);

        PartnerInfoResp resp = partnerService.updatePartner(partner.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getPartnerType(), resp.getPartnerType());
        assertEquals(expectedResponse.getAddress(), resp.getAddress());
        assertEquals(expectedResponse.getEmail(), resp.getEmail());
        assertEquals(expectedResponse.getPhone(), resp.getPhone());
    }

    @Test
    void updatePartnerNullValues() {
        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("TestName");
        partner.setPartnerType(PartnerType.SUPPLIER);
        partner.setAddress("TestAddress");
        partner.setEmail("TestEmail");
        partner.setPhone("TestPhone");

        PartnerInfoReq req = new PartnerInfoReq();
        req.setName(null);
        req.setPartnerType(null);
        req.setAddress(null);
        req.setEmail(null);
        req.setPhone(null);

        when(partnerRepository.findByIdAndIsActiveTrue(partner.getId())).thenReturn(Optional.of(partner));

        when(partnerRepository.save(any(Partner.class))).thenReturn(partner);

        PartnerInfoResp expectedResponse = new PartnerInfoResp();
        expectedResponse.setId(partner.getId());
        expectedResponse.setName(partner.getName());
        expectedResponse.setPartnerType(partner.getPartnerType());
        expectedResponse.setAddress(partner.getAddress());
        expectedResponse.setEmail(partner.getEmail());
        expectedResponse.setPhone(partner.getPhone());
        when(objectMapper.convertValue(partner, PartnerInfoResp.class)).thenReturn(expectedResponse);

        PartnerInfoResp resp = partnerService.updatePartner(partner.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getPartnerType(), resp.getPartnerType());
        assertEquals(expectedResponse.getAddress(), resp.getAddress());
        assertEquals(expectedResponse.getEmail(), resp.getEmail());
        assertEquals(expectedResponse.getPhone(), resp.getPhone());
    }

    @Test
    void updatePartnerNotFound() {
        Long nonExistingId = 999L;
        assertPartnerNotFound(nonExistingId);
    }


    @Test
    void deletePartner() {
        Partner partner = new Partner();
        partner.setId(1L);
        when(partnerRepository.findByIdAndIsActiveTrue(partner.getId())).thenReturn(Optional.of(partner));

        partnerService.deletePartner(partner.getId());
        verify(partnerRepository, times(1)).save(any(Partner.class));
        assertEquals(false, partner.getIsActive());
    }

    @Test
    void deletePartnerNotFound() {
        Long nonExistingId = 999L;
        assertPartnerNotFound(nonExistingId);
    }

    private void assertPartnerNotFound(Long partnerId) {
        when(partnerRepository.findByIdAndIsActiveTrue(partnerId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> partnerService.getPartner(partnerId));
    }
}