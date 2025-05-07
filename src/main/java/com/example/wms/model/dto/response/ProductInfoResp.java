package com.example.wms.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductInfoResp {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "Артикул")
    private String sku;

    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Описание")
    private String description;

    @Schema(description = "Вес")
    private BigDecimal weight;

    @Schema(description = "Габариты")
    private String dimensions;

    @Schema(description = "Наименование категории")
    private String categoryName;

    @Schema(description = "Наименование производителя")
    private String manufacturerName;
}


