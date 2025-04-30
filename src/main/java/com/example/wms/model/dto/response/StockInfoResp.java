package com.example.wms.model.dto.response;

import com.example.wms.model.enums.StockStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private Long id;
    private Long quantity;
    private StockStatus status;
    private String productSku;
    private String locationName;
}
