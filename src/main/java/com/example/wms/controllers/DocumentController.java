package com.example.wms.controllers;

import com.example.wms.model.dto.request.DocumentInfoReq;
import com.example.wms.model.dto.response.DocumentInfoResp;
import com.example.wms.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(name = "Документы")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    @Operation(summary = "Создать документ")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public DocumentInfoResp createDocument(@RequestBody @Valid DocumentInfoReq req) {
        return documentService.createDocument(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить документ по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public DocumentInfoResp getDocument(@PathVariable Long id) {
        return documentService.getDocument(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список документов")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public Page<DocumentInfoResp> getAllDocuments(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer perPage,
                                                  @RequestParam(defaultValue = "number") String sort,
                                                  @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                  @RequestParam(required = false) String filter) {
        return documentService.getAllDocuments(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить документ по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public DocumentInfoResp updateDocument(@PathVariable Long id, @RequestBody @Valid DocumentInfoReq req) {
        return documentService.updateDocument(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить документ по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
    }
}
