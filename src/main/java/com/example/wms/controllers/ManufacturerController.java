package com.example.wms.controllers;

import com.example.wms.model.dto.request.ManufacturerInfoReq;
import com.example.wms.model.dto.response.ManufacturerInfoResp;
import com.example.wms.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    @PostMapping
    public ManufacturerInfoResp createManufacturer(@RequestBody ManufacturerInfoReq req) {
        return manufacturerService.createManufacturer(req);
    }

    @GetMapping("/{id}")
    public ManufacturerInfoResp getManufacturer(@PathVariable Long id) {
        return manufacturerService.getManufacturer(id);
    }

    @GetMapping("/all")
    public Page<ManufacturerInfoResp> getAllManufacturers(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer perPage,
                                                    @RequestParam(defaultValue = "name") String sort,
                                                    @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return manufacturerService.getAllManufacturers(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    public ManufacturerInfoResp updateManufacturer(@PathVariable Long id, @RequestBody ManufacturerInfoReq req) {
        return manufacturerService.updateManufacturer(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturer(id);
    }

}
