package com.kaspro.bank.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Date;

@Data
public class IncreaseLimitVO {

    @ApiModelProperty(value = "Mandatory",required = true)
    private String partnerId;

    @ApiModelProperty(value = "Mandatory",required = true)
    private String memberId;

    @ApiModelProperty(value = "Mandatory",required = true)
    private String destination;

    @ApiModelProperty(value = "Mandatory (yyyy-MM-dd)",required = true)
    private String requestDate;

    @ApiModelProperty(value = "Mandatory",required = true)
    private String amount;
}
