package com.example.wms.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductInfoResp {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal weight;
    private String dimensions;
    private String categoryName;
    private String manufacturerName;
}


