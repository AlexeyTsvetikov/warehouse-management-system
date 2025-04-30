package com.example.wms.controllers;

import com.example.wms.model.dto.request.WarehouseInfoReq;
import com.example.wms.model.dto.response.WarehouseInfoResp;
import com.example.wms.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping
    public WarehouseInfoResp createWarehouse(@RequestBody WarehouseInfoReq req) {
        return warehouseService.createWarehouse(req);
    }

    @GetMapping("/{id}")
    public WarehouseInfoResp getWarehouse(@PathVariable Long id) {
        return warehouseService.getWarehouse(id);
    }

    @GetMapping("/all")
    public Page<WarehouseInfoResp> getAllWarehouses(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer perPage,
                                                    @RequestParam(defaultValue = "name") String sort,
                                                    @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return warehouseService.getAllWarehouses(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    public WarehouseInfoResp updateWarehouse(@PathVariable Long id, @RequestBody WarehouseInfoReq req) {
        return warehouseService.updateWarehouse(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
    }

}
