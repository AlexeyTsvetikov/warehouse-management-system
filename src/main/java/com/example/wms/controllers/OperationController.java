package com.example.wms.controllers;

import com.example.wms.model.dto.request.OperationInfoReq;
import com.example.wms.model.dto.response.OperationInfoResp;
import com.example.wms.model.enums.OperationType;
import com.example.wms.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
@RequiredArgsConstructor
@Tag(name = "Операции")
public class OperationController {
    private final OperationService operationService;

    @PostMapping
    @Operation(summary = "Создать операцию")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public OperationInfoResp createOperation(@RequestBody @Valid OperationInfoReq req) {
        return operationService.createOperation(req);
    }

    @PostMapping("/start/{id}")
    @Operation(summary = "Подготовить операцию к выполнению по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public void startOperation(@PathVariable Long id) {
        operationService.startOperation(id);
    }

    @PostMapping("/receiving/{id}")
    @Operation(summary = "Провести операцию приемки товаров по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public OperationInfoResp receivingOperation(@PathVariable Long id) {
        return operationService.receivingOperation(id);
    }

    @PostMapping("/shipping/{id}")
    @Operation(summary = "Провести операцию отгрузки товаров по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public OperationInfoResp shippingOperation(@PathVariable Long id) {
        return operationService.shippingOperation(id);
    }

    @PostMapping("/transfer/{id}")
    @Operation(summary = "Провести операцию перемещения товаров по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public OperationInfoResp transferOperation(@PathVariable Long id) {
        return operationService.transferOperation(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить операцию по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public OperationInfoResp getOperation(@PathVariable Long id) {
        return operationService.getOperation(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список операций")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public Page<OperationInfoResp> getAllOperations(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer perPage,
                                                          @RequestParam(defaultValue = "id") String sort,
                                                          @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                          @RequestParam(required = false) OperationType filter) {
        return operationService.getAllOperations(page, perPage, sort, order, filter);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Отменить операцию по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER') or hasRole('RECEIVER')")
    public void cancelOperation(@PathVariable Long id) {
        operationService.cancelOperation(id);
    }

}
