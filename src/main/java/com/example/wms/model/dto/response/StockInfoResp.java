package com.example.wms.model.dto.response;

import com.example.wms.model.enums.StockStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockInfoResp {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "Количество")
    private Long quantity;

    @Schema(description = "Статус запаса")
    private StockStatus status;

    @Schema(description = "Артикул товара")
    private String productSku;

    @Schema(description = "Наименование локации")
    private String locationName;
}
