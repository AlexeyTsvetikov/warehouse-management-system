package com.example.wms.model.dto.request;

import com.example.wms.model.enums.OperationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private OperationType operationType;
    private Long orderId;
    private Long userId;
    private Long documentId;
}
