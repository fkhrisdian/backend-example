package com.kaspro.bank.vo.BlacklistMsisdn;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class BlacklistMsisdnVO {
    @ApiModelProperty(value = "Mandatory",required = true)
    private String msisdn;

    @ApiModelProperty(value = "Mandatory",required = true)
    private String reason;

    @ApiModelProperty(value = "Mandatory",required = true)
    private String name;

    @ApiModelProperty(value = "Mandatory",required = true)
    private String email;

    @ApiModelProperty(value = "Mandatory",required = true)
    private String va;
}
