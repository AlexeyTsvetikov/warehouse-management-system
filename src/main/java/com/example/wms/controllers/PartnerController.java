package com.example.wms.controllers;

import com.example.wms.model.dto.request.PartnerInfoReq;
import com.example.wms.model.dto.response.PartnerInfoResp;

import com.example.wms.model.enums.PartnerType;
import com.example.wms.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
@Tag(name = "Партнеры")
public class PartnerController {
    private final PartnerService partnerService;

    @PostMapping
    @Operation(summary = "Создать партнера")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public PartnerInfoResp createPartner(@RequestBody @Valid PartnerInfoReq req) {
        return partnerService.createPartner(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить партнера по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public PartnerInfoResp getPartner(@PathVariable Long id) {
        return partnerService.getPartner(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список партнеров")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public Page<PartnerInfoResp> getAllPartners(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer perPage,
                                                @RequestParam(defaultValue = "name") String sort,
                                                @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                @RequestParam(required = false) PartnerType filter) {
        return partnerService.getAllPartners(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить партнера по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public PartnerInfoResp updatePartner(@PathVariable Long id, @RequestBody @Valid PartnerInfoReq req) {
        return partnerService.updatePartner(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить партнера по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public void deleteProduct(@PathVariable Long id) {
        partnerService.deletePartner(id);
    }
}
