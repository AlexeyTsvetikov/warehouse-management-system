package com.example.wms.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventorInfoResp {

    @Schema(description = "id инвентаризации")
    private Long id;

    @Schema(description = "Текущее количество")
    private Integer actualQuantity;

    @Schema(description = "Дата проверки")
    private Instant inventoryDate;

    @Schema(description = "id запаса")
    private Long stockId;
}
