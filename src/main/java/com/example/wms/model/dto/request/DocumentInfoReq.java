package com.example.wms.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInfoReq {
    @NotEmpty
    @Schema(description = "Номер")
    private String number;

    @NotNull
    @Schema(description = "Дата")
    private LocalDate date;

    @Schema(description = "Комментарии")
    private String notes;

    @NotNull
    @Schema(description = "id партнера")
    private Long partnerId;
}
