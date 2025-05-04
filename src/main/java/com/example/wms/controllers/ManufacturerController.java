package com.example.wms.controllers;

import com.example.wms.model.dto.request.ManufacturerInfoReq;
import com.example.wms.model.dto.response.ManufacturerInfoResp;
import com.example.wms.service.ManufacturerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
@Tag(name = "Производители")
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    @PostMapping
    @Operation(summary = "Создать производителя")
    public ManufacturerInfoResp createManufacturer(@RequestBody @Valid ManufacturerInfoReq req) {
        return manufacturerService.createManufacturer(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить производителя по id")
    public ManufacturerInfoResp getManufacturer(@PathVariable Long id) {
        return manufacturerService.getManufacturer(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список производителей")
    public Page<ManufacturerInfoResp> getAllManufacturers(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer perPage,
                                                          @RequestParam(defaultValue = "name") String sort,
                                                          @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return manufacturerService.getAllManufacturers(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить производителя по id")
    public ManufacturerInfoResp updateManufacturer(@PathVariable Long id, @RequestBody @Valid ManufacturerInfoReq req) {
        return manufacturerService.updateManufacturer(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить производителя по id")
    public void deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturer(id);
    }

}
