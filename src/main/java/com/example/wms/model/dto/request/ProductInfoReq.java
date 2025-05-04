package com.example.wms.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInfoReq {
    @NotEmpty
    @Schema(description = "Артикул")
    private String sku;

    @NotEmpty
    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Описание")
    private String description;

    @NotNull
    @Schema(description = "Вес")
    private BigDecimal weight;

    @Schema(description = "Габариты")
    private String dimensions;

    @NotEmpty
    @Schema(description = "Наименование категории")
    private String categoryName;

    @NotEmpty
    @Schema(description = "Наименование производителя")
    private String manufacturerName;
}
