package com.example.wms.model.dto.response;

import com.example.wms.model.enums.OperationStatus;
import com.example.wms.model.enums.OperationType;
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
public class OperationInfoResp {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "Тип операции")
    private OperationType operationType;

    @Schema(description = "Статус операции")
    private OperationStatus operationStatus;

    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Номер документа")
    private String documentNumber;
}
