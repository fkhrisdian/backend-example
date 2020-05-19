package com.kaspro.bank.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PartnerResponseVO extends BaseVO {
    private String transID;
    private String serviceName;
    private String id;
    private int status;
}
