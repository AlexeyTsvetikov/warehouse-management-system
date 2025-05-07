package com.example.wms.model.dto.response;

import com.example.wms.model.enums.PartnerType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartnerInfoResp {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "Имя")
    private String name;

    @Schema(description = "Тип партнера")
    private PartnerType partnerType;

    @Schema(description = "Адрес")
    private String address;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Телефон")
    private String phone;
}
