package com.example.wms.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
public class ManufacturerInfoReq {
    @NotEmpty
    @Schema(description = "Наименование")
    private String name;

    @NotEmpty
    @Schema(description = "Адрес")
    private String address;

    @NotEmpty
    @Schema(description = "Email")
    private String email;

    @NotEmpty
    @Schema(description = "Телефон")
    private String phone;
}
