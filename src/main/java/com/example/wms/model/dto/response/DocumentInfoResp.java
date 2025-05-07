package com.example.wms.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentInfoResp {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "Номер")
    private String number;

    @Schema(description = "Дата")
    private LocalDate date;

    @Schema(description = "Комментарии")
    private String notes;

    @Schema(description = "Имя партнера")
    private String partnerName;
}
