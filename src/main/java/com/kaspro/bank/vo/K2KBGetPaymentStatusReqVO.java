package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class K2KBGetPaymentStatusReqVO {

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("msisdn")
    private String msisdn;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("form_name")
    private String formName;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("provider")
    private String provider;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("customer_ref_number")
    private String custReff;
}
