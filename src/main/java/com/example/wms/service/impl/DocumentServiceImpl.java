package com.example.wms.service.impl;


import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Document;
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

        final String errMsg = String.format("Partner with id: %s not found", req.getPartnerId());

        if (!partnerRepository.existsByIdAndIsActiveTrue(req.getPartnerId())) {
            throw new CommonBackendException(errMsg, HttpStatus.NOT_FOUND);
        }

        Document document = objectMapper.convertValue(req, Document.class);
        document.setIsActive(true);

        Document savedDocument = documentRepository.save(document);

        return objectMapper.convertValue(savedDocument, DocumentInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentInfoResp getDocument(Long id) {
        final String errMsg = String.format("Document with id: %s not found", id);

        Document document = documentRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        return objectMapper.convertValue(document, DocumentInfoResp.class);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<DocumentInfoResp> getAllDocuments(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);

        Page<Document> documents = documentRepository.findAllByIsActiveTrue(pageRequest);

        List<DocumentInfoResp> content = documents.getContent().stream()
                .map(document -> objectMapper.convertValue(document, DocumentInfoResp.class))
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
        return objectMapper.convertValue(updatedDocument, DocumentInfoResp.class);
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