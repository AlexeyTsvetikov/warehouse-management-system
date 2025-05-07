package com.example.wms.model.dto.response;


import com.example.wms.model.enums.LocationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationInfoResp {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "Наименование склада")
    private String warehouseName;

    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Тип локации")
    private LocationType locationType;

    @Schema(description = "Вместимость")
    private Long maxCapacity;

    @Schema(description = "Габариты")
    private String dimensions;

    @Schema(description = "Описание")
    private String description;
}