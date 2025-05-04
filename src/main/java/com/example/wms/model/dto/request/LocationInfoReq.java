package com.example.wms.model.dto.request;

import com.example.wms.model.enums.LocationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Schema(description = "id склада")
    private Long warehouseId;

    @NotEmpty
    @Schema(description = "Наименование")
    private String name;

    @NotNull
    @Schema(description = "Тип локации")
    private LocationType locationType;

    @NotNull
    @Schema(description = "Вместимость")
    private Long maxCapacity;

    @NotEmpty
    @Schema(description = "Габариты")
    private String dimensions;

    @Schema(description = "Описание")
    private String description;
}
