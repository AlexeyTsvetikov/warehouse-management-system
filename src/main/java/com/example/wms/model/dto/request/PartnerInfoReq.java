package com.example.wms.model.dto.request;

import com.example.wms.model.enums.PartnerType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class PartnerInfoReq {
    private String name;
    private PartnerType partnerType;
    private String address;
    private String email;
    private String phone;
}
