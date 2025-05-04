package com.example.wms.model.dto.response;

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
public class OperationDetailInfoResp {
    @Schema(description = "id детали опреации")
    private Long id;

    @Schema(description = "id операции")
    private Long operationId;

    @Schema(description = "Артикул")
    private String sku;

    @Schema(description = "Количество")
    private Integer quantity;

    @Schema(description = "Наименование локации отправления")
    private String fromLocationName;

    @Schema(description = "Наименование локации назначения")
    private String toLocationName;
}
