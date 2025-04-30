package com.example.wms.model.dto.request;

import com.example.wms.model.enums.LocationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationInfoReq {
    private Long warehouseId;
    private String name;
    private LocationType locationType;
    private Long maxCapacity;
    private String dimensions;
    private String description;
}
