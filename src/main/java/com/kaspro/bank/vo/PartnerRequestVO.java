package com.kaspro.bank.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PartnerRequestVO extends BaseVO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name min 2 and max 255 character")
    private String name;
}
