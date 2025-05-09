package com.example.wms.controllers;

import com.example.wms.model.dto.request.LocationInfoReq;
import com.example.wms.model.dto.response.LocationInfoResp;
import com.example.wms.model.enums.LocationType;
import com.example.wms.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Tag(name = "Локации")
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "Создать локацию")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public LocationInfoResp createLocation(@RequestBody @Valid LocationInfoReq req) {
        return locationService.createLocation(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить локацию по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER')" +
            " or hasRole('RECEIVER') or ('LOADER')")
    public LocationInfoResp getLocation(@PathVariable Long id) {
        return locationService.getLocation(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список локаций")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER') or hasRole('PICKER')" +
            " or hasRole('RECEIVER') or ('LOADER')")
    public Page<LocationInfoResp> getAllLocations(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer perPage,
                                                  @RequestParam(defaultValue = "name") String sort,
                                                  @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                                  @RequestParam(required = false) LocationType filter) {
        return locationService.getAllLocations(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить локацию по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STOREKEEPER')")
    public LocationInfoResp updateLocation(@PathVariable Long id, @RequestBody @Valid LocationInfoReq req) {
        return locationService.updateLocation(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить локацию по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public void deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
    }
}
