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
public class UserInfoReq {
    @NotEmpty
    @Schema(description = "Логин")
    private String username;

    @NotEmpty
    @Schema(description = "Хеш пароля")
    private String password;

    @NotEmpty
    @Schema(description = "Имя")
    private String firstName;

    @NotEmpty
    @Schema(description = "Фамилия")
    private String lastName;

    @NotEmpty
    @Schema(description = "Отчество")
    private String middleName;

    @NotEmpty
    @Schema(description = "Наименование роли")
    private String roleName;
}
