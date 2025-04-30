package com.example.wms.model.dto.response;


import com.example.wms.model.enums.LocationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationInfoResp {
    private Long id;
    private String warehouseName;
    private String name;
    private LocationType locationType;
    private Long maxCapacity;
    private String dimensions;
    private String description;
}