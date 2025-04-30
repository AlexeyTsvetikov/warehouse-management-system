package com.example.wms.controllers;

import com.example.wms.model.dto.request.LocationInfoReq;
import com.example.wms.model.dto.response.LocationInfoResp;
import com.example.wms.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public LocationInfoResp createLocation(@RequestBody LocationInfoReq req) {
        return locationService.createLocation(req);
    }

    @GetMapping("/{id}")
    public LocationInfoResp getLocation(@PathVariable Long id) {
        return locationService.getLocation(id);
    }

    @GetMapping("/all")
    public Page<LocationInfoResp> getAllLocations(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer perPage,
                                                    @RequestParam(defaultValue = "name") String sort,
                                                    @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return locationService.getAllLocations(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    public LocationInfoResp updateLocation(@PathVariable Long id, @RequestBody LocationInfoReq req) {
        return locationService.updateLocation(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
    }
}
