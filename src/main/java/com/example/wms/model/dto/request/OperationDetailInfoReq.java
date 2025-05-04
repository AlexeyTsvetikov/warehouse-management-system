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

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationDetailInfoReq {
    @NotNull
    @Schema(description = "id операции")
    private Long operationId;

    @NotEmpty
    @Schema(description = "Артикул")
    private String sku;

    @NotNull
    @Schema(description = "Количество")
    private Integer quantity;

    @NotEmpty
    @Schema(description = "Наименование локации отправления")
    private String fromLocationName;

    @NotEmpty
    @Schema(description = "Наименование локации назначения")
    private String toLocationName;
}
