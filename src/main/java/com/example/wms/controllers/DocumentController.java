package com.example.wms.controllers;

import com.example.wms.model.dto.request.DocumentInfoReq;
import com.example.wms.model.dto.response.DocumentInfoResp;
import com.example.wms.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public DocumentInfoResp createDocument(@RequestBody DocumentInfoReq req) {
        return documentService.createDocument(req);
    }

    @GetMapping("/{id}")
    public DocumentInfoResp getDocument(@PathVariable Long id) {
        return documentService.getDocument(id);
    }

    @GetMapping("/all")
    public Page<DocumentInfoResp> getAllDocuments(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer perPage,
                                                  @RequestParam(defaultValue = "number") String sort,
                                                  @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                  @RequestParam(required = false) String filter) {
        return documentService.getAllDocuments(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    public DocumentInfoResp updateDocument(@PathVariable Long id, @RequestBody DocumentInfoReq req) {
        return documentService.updateDocument(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
    }
}
