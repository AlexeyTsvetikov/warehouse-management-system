package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Location;
import com.example.wms.model.db.entity.Warehouse;
import com.example.wms.model.db.repository.LocationRepository;
import com.example.wms.model.db.repository.WarehouseRepository;
import com.example.wms.model.dto.request.LocationInfoReq;
import com.example.wms.model.dto.response.DocumentInfoResp;
import com.example.wms.model.dto.response.LocationInfoResp;
import com.example.wms.model.enums.LocationType;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createLocation() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("TestWarehouse");

        LocationInfoReq req = new LocationInfoReq();
        req.setWarehouseId(1L);
        req.setName("TestName");
        req.setLocationType(LocationType.STORAGE);
        req.setMaxCapacity(100L);
        req.setDescription("TestDescription");
        req.setDimensions("TestDimensions");

        Location location = new Location();
        location.setWarehouse(warehouse);
        location.setName(req.getName());
        location.setLocationType(req.getLocationType());
        location.setMaxCapacity(req.getMaxCapacity());
        location.setDescription(req.getDescription());
        location.setDimensions(req.getDimensions());

        when(locationRepository.findByName(req.getName())).thenReturn(Optional.empty());

        when(warehouseRepository.findByIdAndIsActiveTrue(req.getWarehouseId())).thenReturn(Optional.of(warehouse));

        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationInfoResp locationInfoResp = locationService.createLocation(req);

        assertEquals(location.getWarehouse().getName(), locationInfoResp.getWarehouseName());
        assertEquals(location.getName(), locationInfoResp.getName());
        assertEquals(location.getLocationType(), locationInfoResp.getLocationType());
        assertEquals(location.getMaxCapacity(), locationInfoResp.getMaxCapacity());
        assertEquals(location.getDescription(), locationInfoResp.getDescription());
        assertEquals(location.getDimensions(), locationInfoResp.getDimensions());
    }

    @Test
    void createLocationExists() {
        LocationInfoReq req = new LocationInfoReq();
        req.setName("ExistingName");

        Location existingLocation = new Location();
        existingLocation.setName("ExistingName");

        when(locationRepository.findByName(req.getName())).thenReturn(Optional.of(existingLocation));

        assertThrows(CommonBackendException.class, () -> locationService.createLocation(req));
    }

    @Test
    void createLocationNotFoundWarehouse() {
        LocationInfoReq req = new LocationInfoReq();
        req.setWarehouseId(999L);

        when(warehouseRepository.findByIdAndIsActiveTrue(req.getWarehouseId())).thenReturn(Optional.empty());

        assertThrows(CommonBackendException.class, () -> locationService.createLocation(req));
    }

    @Test
    void createLocationNullWarehouse() {
        LocationInfoReq req = new LocationInfoReq();
        req.setWarehouseId(null);

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                locationService.createLocation(req));
        assertEquals("Warehouse must be provided", exception.getMessage());
    }

    @Test
    void getLocation() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("TestWarehouse");

        Location location = new Location();
        location.setId(1L);
        location.setWarehouse(warehouse);
        location.setName("TestName");
        location.setLocationType(LocationType.STORAGE);
        location.setMaxCapacity(100L);
        location.setDimensions("TestDimensions");
        location.setDescription("TestDescription");
        location.setIsActive(true);

        when(locationRepository.findByIdAndIsActiveTrue(location.getId())).thenReturn(Optional.of(location));

        LocationInfoResp locationInfoResp = locationService.getLocation(location.getId());
        assertEquals(location.getId(), locationInfoResp.getId());
        assertEquals(location.getWarehouse().getName(), locationInfoResp.getWarehouseName());
        assertEquals(location.getName(), locationInfoResp.getName());
        assertEquals(location.getLocationType(), locationInfoResp.getLocationType());
        assertEquals(location.getMaxCapacity(), locationInfoResp.getMaxCapacity());
        assertEquals(location.getDescription(), locationInfoResp.getDescription());
        assertEquals(location.getDimensions(), locationInfoResp.getDimensions());
    }

    @Test
    void getLocationNotFound() {
        Long nonExistingId = 999L;
        assertLocationNotFound(nonExistingId);
    }

    @Test
    void getAllLocationsWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String filter = "STORAGE";

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Location location = new Location();
        location.setWarehouse(warehouse);
        LocationInfoResp locationInfoResp = new LocationInfoResp();

        when(locationRepository.findAllFiltered(LocationType.valueOf(filter), pageable)).thenReturn(new PageImpl<>(List.of(location)));
        when(objectMapper.convertValue(location, LocationInfoResp.class)).thenReturn(locationInfoResp);

        Page<LocationInfoResp> result = locationService.getAllLocations(pageNumber, pageSize, sortField, sortDirection, LocationType.valueOf(filter));

        assertEquals(1, result.getContent().size());
        assertEquals(locationInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllLocationsWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Location location = new Location();
        location.setWarehouse(warehouse);
        LocationInfoResp locationInfoResp = new LocationInfoResp();

        when(locationRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(location)));
        when(objectMapper.convertValue(location, LocationInfoResp.class)).thenReturn(locationInfoResp);

        Page<LocationInfoResp> result = locationService.getAllLocations(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(locationInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateLocationAllValues() {
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setId(1L);
        warehouse1.setName("TestWarehouse1");

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setId(2L);
        warehouse2.setName("TestWarehouse2");

        Location location = new Location();
        location.setId(1L);
        location.setWarehouse(warehouse1);
        location.setName("TestName");
        location.setLocationType(LocationType.STORAGE);
        location.setMaxCapacity(100L);
        location.setDimensions("TestDimensions");
        location.setDescription("TestDescription");
        location.setIsActive(true);

        LocationInfoReq req = new LocationInfoReq();
        req.setWarehouseId(warehouse2.getId());
        req.setName("NewName");
        req.setLocationType(LocationType.DISPATCH);
        req.setMaxCapacity(50L);
        req.setDimensions("NewDimensions");
        req.setDescription("NewDescription");

        when(locationRepository.findByIdAndIsActiveTrue(location.getId())).thenReturn(Optional.of(location));

        when(warehouseRepository.findByIdAndIsActiveTrue(req.getWarehouseId())).thenReturn(Optional.of(warehouse2));

        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationInfoResp expectedResponse = new LocationInfoResp();
        expectedResponse.setId(location.getId());
        expectedResponse.setName(req.getName());
        expectedResponse.setLocationType(req.getLocationType());
        expectedResponse.setDescription(req.getDescription());
        expectedResponse.setDimensions(req.getDimensions());
        expectedResponse.setMaxCapacity(req.getMaxCapacity());
        expectedResponse.setWarehouseName(warehouse2.getName());

        LocationInfoResp resp = locationService.updateLocation(location.getId(), req);

        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getLocationType(), resp.getLocationType());
        assertEquals(expectedResponse.getMaxCapacity(), resp.getMaxCapacity());
        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getDimensions(), resp.getDimensions());
        assertEquals(expectedResponse.getDescription(), resp.getDescription());
        assertEquals(expectedResponse.getWarehouseName(), resp.getWarehouseName());
    }

    @Test
    void updateLocationNullValues() {
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setId(1L);
        warehouse1.setName("TestWarehouse1");

        Location location = new Location();
        location.setId(1L);
        location.setWarehouse(warehouse1);
        location.setName("TestName");
        location.setLocationType(LocationType.STORAGE);
        location.setMaxCapacity(100L);
        location.setDimensions("TestDimensions");
        location.setDescription("TestDescription");
        location.setIsActive(true);

        LocationInfoReq req = new LocationInfoReq();
        req.setWarehouseId(null);
        req.setName(null);
        req.setLocationType(null);
        req.setMaxCapacity(null);
        req.setDimensions(null);
        req.setDescription(null);

        when(locationRepository.findByIdAndIsActiveTrue(location.getId())).thenReturn(Optional.of(location));

        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationInfoResp expectedResponse = new LocationInfoResp();
        expectedResponse.setId(location.getId());
        expectedResponse.setName(location.getName());
        expectedResponse.setLocationType(location.getLocationType());
        expectedResponse.setDescription(location.getDescription());
        expectedResponse.setDimensions(location.getDimensions());
        expectedResponse.setMaxCapacity(location.getMaxCapacity());
        expectedResponse.setWarehouseName(location.getWarehouse().getName());

        LocationInfoResp resp = locationService.updateLocation(location.getId(), req);

        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getLocationType(), resp.getLocationType());
        assertEquals(expectedResponse.getMaxCapacity(), resp.getMaxCapacity());
        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getDimensions(), resp.getDimensions());
        assertEquals(expectedResponse.getDescription(), resp.getDescription());
        assertEquals(expectedResponse.getWarehouseName(), resp.getWarehouseName());
    }

    @Test
    void updateLocationWarehouseChanged() {
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setId(1L);
        warehouse1.setName("TestWarehouse1");

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setId(2L);
        warehouse2.setName("TestWarehouse2");

        Location location = new Location();
        location.setId(1L);
        location.setWarehouse(warehouse1);
        location.setName("TestName");
        location.setLocationType(LocationType.STORAGE);
        location.setMaxCapacity(100L);
        location.setDimensions("TestDimensions");
        location.setDescription("TestDescription");
        location.setIsActive(true);

        LocationInfoReq req = new LocationInfoReq();
        req.setWarehouseId(warehouse2.getId());
        req.setName(null);
        req.setLocationType(null);
        req.setMaxCapacity(null);
        req.setDimensions(null);
        req.setDescription(null);

        when(locationRepository.findByIdAndIsActiveTrue(location.getId())).thenReturn(Optional.of(location));
        when(warehouseRepository.findByIdAndIsActiveTrue(warehouse2.getId())).thenReturn(Optional.of(warehouse2));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationInfoResp resp = locationService.updateLocation(location.getId(), req);

        assertEquals(location.getWarehouse().getName(), resp.getWarehouseName());
        assertEquals(location.getName(), resp.getName());
        assertEquals(location.getLocationType(), resp.getLocationType());
        assertEquals(location.getMaxCapacity(), resp.getMaxCapacity());
        assertEquals(location.getDimensions(), resp.getDimensions());
        assertEquals(location.getDescription(), resp.getDescription());
    }

    @Test
    void updateLocationWarehouseNotFound() {
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setId(1L);
        warehouse1.setName("TestWarehouse1");

        Location location = new Location();
        location.setId(1L);
        location.setWarehouse(warehouse1);
        location.setName("TestName");
        location.setLocationType(LocationType.STORAGE);
        location.setMaxCapacity(100L);
        location.setDimensions("TestDimensions");
        location.setDescription("TestDescription");
        location.setIsActive(true);

        LocationInfoReq req = new LocationInfoReq();
        req.setWarehouseId(2L);
        req.setName(null);
        req.setLocationType(null);
        req.setMaxCapacity(null);
        req.setDimensions(null);
        req.setDescription(null);

        when(locationRepository.findByIdAndIsActiveTrue(location.getId())).thenReturn(Optional.of(location));
        when(warehouseRepository.findByIdAndIsActiveTrue(req.getWarehouseId())).thenReturn(Optional.empty());

        CommonBackendException ex = assertThrows(CommonBackendException.class, () ->
                locationService.updateLocation(location.getId(), req));

        assertEquals("Warehouse with id : " + req.getWarehouseId() + " not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void updateLocationNotFound() {
        Long nonExistingId = 999L;
        assertLocationNotFound(nonExistingId);
    }

    @Test
    void deleteLocation() {
        Location location = new Location();
        location.setId(1L);
        when(locationRepository.findByIdAndIsActiveTrue(location.getId())).thenReturn(Optional.of(location));

        locationService.deleteLocation(location.getId());
        verify(locationRepository, times(1)).save(any(Location.class));
        assertEquals(false, location.getIsActive());
    }

    @Test
    void deleteLocationNotFound() {
        Long nonExistingId = 999L;
        assertLocationNotFound(nonExistingId);
    }

    private void assertLocationNotFound(Long locationId) {
        when(locationRepository.findByIdAndIsActiveTrue(locationId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> locationService.getLocation(locationId));
    }
}