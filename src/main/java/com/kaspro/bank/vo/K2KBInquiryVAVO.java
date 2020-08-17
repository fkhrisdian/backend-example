package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class K2KBInquiryVAVO {

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("msisdn")
    private String msisdn;
}
