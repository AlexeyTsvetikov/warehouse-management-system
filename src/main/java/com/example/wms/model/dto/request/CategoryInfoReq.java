package com.example.wms.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryInfoReq {
    @NotEmpty
    @Schema(description = "Наименование")
    private String name;

    @NotEmpty
    @Schema(description = "Описание")
    private String description;
}
