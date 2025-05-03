package com.example.wms.controllers;

import com.example.wms.model.dto.request.OperationInfoReq;
import com.example.wms.model.dto.response.OperationInfoResp;
import com.example.wms.model.enums.OperationType;
import com.example.wms.service.OperationService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;

    @PostMapping
    public OperationInfoResp createOperation(@RequestBody OperationInfoReq req) {
        return operationService.createOperation(req);
    }

    @PostMapping("/start/{id}")
    public void startOperation(@PathVariable Long id) {
        operationService.startOperation(id);
    }

    @PostMapping("/receiving/{id}")
    public OperationInfoResp receivingOperation(@PathVariable Long id) {
        return operationService.receivingOperation(id);
    }

    @PostMapping("/shipping/{id}")
    public OperationInfoResp shippingOperation(@PathVariable Long id) {
        return operationService.shippingOperation(id);
    }

    @PostMapping("/transfer/{id}")
    public OperationInfoResp transferOperation(@PathVariable Long id) {
        return operationService.transferOperation(id);
    }

    @GetMapping("/{id}")
    public OperationInfoResp getOperation(@PathVariable Long id) {
        return operationService.getOperation(id);
    }

    @GetMapping("/all")
    public Page<OperationInfoResp> getAllOperations(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer perPage,
                                                          @RequestParam(defaultValue = "id") String sort,
                                                          @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                          @RequestParam(required = false) OperationType filter) {
        return operationService.getAllOperations(page, perPage, sort, order, filter);
    }

    @DeleteMapping("/{id}")
    public void cancelOperation(@PathVariable Long id) {
        operationService.cancelOperation(id);
    }

}
