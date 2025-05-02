package com.example.wms.controllers;

import com.example.wms.model.dto.request.OperationDetailInfoReq;
import com.example.wms.model.dto.response.OperationDetailInfoResp;
import com.example.wms.service.OperationDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operationDetails")
@RequiredArgsConstructor
public class OperationDetailController {
    private final OperationDetailService operationDetailService;

    @PostMapping
    public OperationDetailInfoResp createOperationDetail(@RequestBody OperationDetailInfoReq req) {
        return operationDetailService.createOperationDetail(req);
    }

    @GetMapping("/{id}")
    public OperationDetailInfoResp getOperationDetail(@PathVariable Long id) {
        return operationDetailService.getDetail(id);
    }

    @GetMapping("/all")
    public Page<OperationDetailInfoResp> getAllOperationDetails(@RequestParam(defaultValue = "1") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer perPage,
                                                                @RequestParam(defaultValue = "id") String sort,
                                                                @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return operationDetailService.getAllOperationDetails(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    public OperationDetailInfoResp updateOperationDetail(@PathVariable Long id, @RequestBody OperationDetailInfoReq req) {
        return operationDetailService.updateOperationDetail(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteOperationDetail(@PathVariable Long id) {
        operationDetailService.deleteOperationDetail(id);
    }

}
