package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Document;
import com.example.wms.model.db.entity.Partner;
import com.example.wms.model.db.repository.DocumentRepository;
import com.example.wms.model.db.repository.PartnerRepository;
import com.example.wms.model.dto.request.DocumentInfoReq;
import com.example.wms.model.dto.response.DocumentInfoResp;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createDocument() {
        DocumentInfoReq req = new DocumentInfoReq();
        req.setNumber("12345");
        req.setDate(LocalDate.of(2025, 4, 29));
        req.setNotes("TestNotes");
        req.setPartnerId(1L);

        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("TestPartner");

        Document document = new Document();
        document.setNumber(req.getNumber());
        document.setDate(req.getDate());
        document.setNotes(req.getNotes());
        document.setPartner(partner);

        when(documentRepository.findByNumber(req.getNumber())).thenReturn(Optional.empty());

        when(partnerRepository.findByIdAndIsActiveTrue(req.getPartnerId())).thenReturn(Optional.of(partner));

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentInfoResp documentInfoResp = documentService.createDocument(req);

        assertEquals(document.getNumber(), documentInfoResp.getNumber());
        assertEquals(document.getDate(), documentInfoResp.getDate());
        assertEquals(document.getNotes(), documentInfoResp.getNotes());
        assertEquals(document.getPartner().getName(), documentInfoResp.getPartnerName());
    }

    @Test
    void createDocumentExists() {
        DocumentInfoReq req = new DocumentInfoReq();
        req.setNumber("ExistingNumber");

        Document existingDocument = new Document();
        existingDocument.setNumber("ExistingNumber");

        when(documentRepository.findByNumber(req.getNumber())).thenReturn(Optional.of(existingDocument));

        assertThrows(CommonBackendException.class, () -> documentService.createDocument(req));
    }

    @Test
    void createDocumentNotFoundPartner() {
        DocumentInfoReq req = new DocumentInfoReq();
        req.setPartnerId(999L);

        when(partnerRepository.findByIdAndIsActiveTrue(req.getPartnerId())).thenReturn(Optional.empty());

        assertThrows(CommonBackendException.class, () -> documentService.createDocument(req));
    }

    @Test
    void createDocumentNullPartner() {
        DocumentInfoReq req = new DocumentInfoReq();
        req.setNumber("12345");
        req.setPartnerId(null);

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                documentService.createDocument(req));
        assertEquals("Partner must be provided", exception.getMessage());
    }

    @Test
    void getDocument() {
        Partner partner = new Partner();
        partner.setName("TestPartner");

        Document document = new Document();
        document.setId(1L);
        document.setNumber("12345");
        document.setDate(LocalDate.of(2025, 4, 29));
        document.setNotes("TestNotes");
        document.setPartner(partner);
        document.setIsActive(true);

        when(documentRepository.findByIdAndIsActiveTrue(document.getId())).thenReturn(Optional.of(document));

        DocumentInfoResp documentInfoResp = documentService.getDocument(document.getId());
        assertEquals(document.getId(), documentInfoResp.getId());
        assertEquals(document.getNumber(), documentInfoResp.getNumber());
        assertEquals(document.getDate(), documentInfoResp.getDate());
        assertEquals(document.getNotes(), documentInfoResp.getNotes());
        assertEquals(document.getPartner().getName(), documentInfoResp.getPartnerName());
    }

    @Test
    void getDocumentNotFound() {
        Long nonExistingId = 999L;
        assertDocumentNotFound(nonExistingId);
    }

    @Test
    void getAllDocumentsWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String filter = "active";

        Partner partner = new Partner();
        partner.setName("TestPartner");

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Document document = new Document();
        document.setPartner(partner);
        DocumentInfoResp documentInfoResp = new DocumentInfoResp();

        when(documentRepository.findAllFiltered(pageable, filter)).thenReturn(new PageImpl<>(List.of(document)));
        when(objectMapper.convertValue(document, DocumentInfoResp.class)).thenReturn(documentInfoResp);

        Page<DocumentInfoResp> result = documentService.getAllDocuments(pageNumber, pageSize, sortField, sortDirection, filter);

        assertEquals(1, result.getContent().size());
        assertEquals(documentInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllDocumentsWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Partner partner = new Partner();
        partner.setName("TestPartner");

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Document document = new Document();
        document.setPartner(partner);
        DocumentInfoResp documentInfoResp = new DocumentInfoResp();

        when(documentRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(document)));
        when(objectMapper.convertValue(document, DocumentInfoResp.class)).thenReturn(documentInfoResp);

        Page<DocumentInfoResp> result = documentService.getAllDocuments(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(documentInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }


    @Test
    void updateDocumentAllValues() {
        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("TestPartner");


        Document document = new Document();
        document.setId(1L);
        document.setNumber("12345");
        document.setDate(LocalDate.of(2025, 4, 29));
        document.setNotes("TestNotes");
        document.setPartner(partner);
        document.setIsActive(true);

        DocumentInfoReq req = new DocumentInfoReq();
        req.setNumber("54321");
        req.setDate(LocalDate.of(2025, 5, 5));
        req.setNotes("NewNotes");

        when(documentRepository.findByIdAndIsActiveTrue(document.getId())).thenReturn(Optional.of(document));

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentInfoResp expectedResponse = new DocumentInfoResp();
        expectedResponse.setId(document.getId());
        expectedResponse.setNumber(req.getNumber());
        expectedResponse.setDate(req.getDate());
        expectedResponse.setNotes(req.getNotes());
        when(objectMapper.convertValue(document, DocumentInfoResp.class)).thenReturn(expectedResponse);

        DocumentInfoResp resp = documentService.updateDocument(document.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getNumber(), resp.getNumber());
        assertEquals(expectedResponse.getDate(), resp.getDate());
        assertEquals(expectedResponse.getNotes(), resp.getNotes());
    }

    @Test
    void updateDocumentNullValues() {
        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("TestPartner");


        Document document = new Document();
        document.setId(1L);
        document.setNumber("12345");
        document.setDate(LocalDate.of(2025, 4, 29));
        document.setNotes("TestNotes");
        document.setPartner(partner);
        document.setIsActive(true);

        DocumentInfoReq req = new DocumentInfoReq();
        req.setNumber(null);
        req.setDate(null);
        req.setNotes(null);

        when(documentRepository.findByIdAndIsActiveTrue(document.getId())).thenReturn(Optional.of(document));

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentInfoResp expectedResponse = new DocumentInfoResp();
        expectedResponse.setId(document.getId());
        expectedResponse.setNumber(document.getNumber());
        expectedResponse.setDate(document.getDate());
        expectedResponse.setNotes(document.getNotes());
        when(objectMapper.convertValue(document, DocumentInfoResp.class)).thenReturn(expectedResponse);

        DocumentInfoResp resp = documentService.updateDocument(document.getId(), req);

        assertEquals(expectedResponse.getNumber(), resp.getNumber());
        assertEquals(expectedResponse.getDate(), resp.getDate());
        assertEquals(expectedResponse.getNotes(), resp.getNotes());
    }

    @Test
    void updateDocumentNotFound() {
        Long nonExistingId = 999L;
        assertDocumentNotFound(nonExistingId);
    }

    @Test
    void deleteDocument() {
        Document document = new Document();
        document.setId(1L);
        when(documentRepository.findByIdAndIsActiveTrue(document.getId())).thenReturn(Optional.of(document));

        documentService.deleteDocument(document.getId());
        verify(documentRepository, times(1)).save(any(Document.class));
        assertEquals(false, document.getIsActive());
    }

    @Test
    void deleteDocumentNotFound() {
        Long nonExistingId = 999L;
        assertDocumentNotFound(nonExistingId);
    }

    private void assertDocumentNotFound(Long documentId) {
        when(documentRepository.findByIdAndIsActiveTrue(documentId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> documentService.getDocument(documentId));
    }
}