package com.example.wms.service;

import com.example.wms.model.dto.request.DocumentInfoReq;
import com.example.wms.model.dto.response.DocumentInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface DocumentService {
    @Transactional
    DocumentInfoResp createDocument(DocumentInfoReq req);

    @Transactional(readOnly = true)
    DocumentInfoResp getDocument(Long id);

    @Transactional(readOnly = true)
    Page<DocumentInfoResp> getAllDocuments(Integer page, Integer perPage, String sort, Sort.Direction order);

    @Transactional
    DocumentInfoResp updateDocument(Long id, DocumentInfoReq req);

    @Transactional
    void deleteDocument(Long id);
}
