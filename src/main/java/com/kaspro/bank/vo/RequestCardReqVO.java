package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class RequestCardReqVO {
    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("msisdn")
    private String msisdn;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("recipient_name")
    private String name;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("recipient_address")
    private String address;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("recipient_city")
    private String city;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("recipient_province")
    private String province;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("recipient_zip_code")
    private String zipCode;
}
