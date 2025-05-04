package com.example.wms.controllers;

import com.example.wms.model.dto.request.OperationDetailInfoReq;
import com.example.wms.model.dto.response.OperationDetailInfoResp;
import com.example.wms.service.OperationDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operationDetails")
@RequiredArgsConstructor
@Tag(name = "Детали операции")
public class OperationDetailController {
    private final OperationDetailService operationDetailService;

    @PostMapping
    @Operation(summary = "Создать детали операции")
    public OperationDetailInfoResp createOperationDetail(@RequestBody @Valid OperationDetailInfoReq req) {
        return operationDetailService.createOperationDetail(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить детали операции по id")
    public OperationDetailInfoResp getOperationDetail(@PathVariable Long id) {
        return operationDetailService.getDetail(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список деталей операций")
    public Page<OperationDetailInfoResp> getAllOperationDetails(@RequestParam(defaultValue = "1") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer perPage,
                                                                @RequestParam(defaultValue = "id") String sort,
                                                                @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                                @RequestParam(required = false) Long filter) {
        return operationDetailService.getAllOperationDetails(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить детали операции по id")
    public OperationDetailInfoResp updateOperationDetail(@PathVariable Long id, @RequestBody @Valid OperationDetailInfoReq req) {
        return operationDetailService.updateOperationDetail(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить детали операции по id")
    public void deleteOperationDetail(@PathVariable Long id) {
        operationDetailService.deleteOperationDetail(id);
    }

}
