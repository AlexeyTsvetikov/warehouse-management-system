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
public class ManufacturerInfoResp {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Адрес")
    private String address;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Телефон")
    private String phone;
}
