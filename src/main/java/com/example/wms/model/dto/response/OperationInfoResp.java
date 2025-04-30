package com.example.wms.model.dto.response;

import com.example.wms.model.enums.OperationStatus;
import com.example.wms.model.enums.OperationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationInfoResp {
    private Long id;
    private OperationType operationType;
    private OperationStatus operationStatus;
    private String username;
    private String documentNumber;
    private List<OperationDetailInfoResp> operationDetails;
}
