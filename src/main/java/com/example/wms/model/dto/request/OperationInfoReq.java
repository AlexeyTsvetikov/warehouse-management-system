package com.example.wms.model.dto.request;

import com.example.wms.model.enums.OperationType;
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
public class OperationInfoReq {
    @NotNull
    @Schema(description = "Тип операции")
    private OperationType operationType;

    @NotNull
    @Schema(description = "id пользователя")
    private Long userId;

    @NotEmpty
    @Schema(description = "id документа")
    private Long documentId;
}
