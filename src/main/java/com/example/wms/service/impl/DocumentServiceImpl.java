package com.example.wms.service.impl;


import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Document;
import com.example.wms.model.db.entity.Partner;
import com.example.wms.model.db.repository.DocumentRepository;
import com.example.wms.model.db.repository.PartnerRepository;
import com.example.wms.model.dto.request.DocumentInfoReq;
import com.example.wms.model.dto.response.DocumentInfoResp;
import com.example.wms.service.DocumentService;
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
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final PartnerRepository partnerRepository;

    @Override
    @Transactional
    public DocumentInfoResp createDocument(DocumentInfoReq req) {
        if (documentRepository.findByNumber(req.getNumber()).isPresent()) {
            throw new CommonBackendException("Document with number already exists", HttpStatus.CONFLICT);
        }

        Document document = objectMapper.convertValue(req, Document.class);
        if (req.getPartnerId() != null) {
            Partner partner = partnerRepository.findByIdAndIsActiveTrue(req.getPartnerId())
                    .orElseThrow(() -> new CommonBackendException(
                            "Partner with id : " + req.getPartnerId() + " not found", HttpStatus.NOT_FOUND));
            document.setPartner(partner);
        } else {
            throw new CommonBackendException("Partner must be provided", HttpStatus.BAD_REQUEST);
        }

        document.setIsActive(true);
        Document savedDocument = documentRepository.save(document);

        DocumentInfoResp resp = objectMapper.convertValue(savedDocument, DocumentInfoResp.class);
        resp.setPartnerName(savedDocument.getPartner().getName());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentInfoResp getDocument(Long id) {
        final String errMsg = String.format("Document with id: %s not found", id);

        Document document = documentRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        DocumentInfoResp resp = objectMapper.convertValue(document, DocumentInfoResp.class);
        resp.setPartnerName(document.getPartner().getName());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentInfoResp> getAllDocuments(Integer page, Integer perPage, String sort, Sort.Direction order, String filter) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Document> documents;

        if (StringUtils.hasText(filter)) {
            documents = documentRepository.findAllFiltered(pageRequest, filter);
        } else {
            documents = documentRepository.findAllByIsActiveTrue(pageRequest);
        }

        List<DocumentInfoResp> content = documents.getContent().stream()
                .map(document -> {
                    DocumentInfoResp resp = objectMapper.convertValue(document, DocumentInfoResp.class);
                    resp.setPartnerName(document.getPartner().getName());
                    return resp;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, documents.getTotalElements());
    }

    @Override
    @Transactional
    public DocumentInfoResp updateDocument(Long id, DocumentInfoReq req) {
        final String errMsg = String.format("Document with id: %s not found", id);

        Document document = documentRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        document.setNumber(req.getNumber() != null ? req.getNumber() : document.getNumber());
        document.setDate(req.getDate() != null ? req.getDate() : document.getDate());
        document.setNotes(req.getNotes() != null ? req.getNotes() : document.getNotes());

        Document updatedDocument = documentRepository.save(document);
        DocumentInfoResp resp = objectMapper.convertValue(updatedDocument, DocumentInfoResp.class);
        resp.setPartnerName(updatedDocument.getPartner().getName());
        return resp;
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        final String errMsg = String.format("Document with id: %s not found", id);

        Document document = documentRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        document.setIsActive(false);
        documentRepository.save(document);
    }
}