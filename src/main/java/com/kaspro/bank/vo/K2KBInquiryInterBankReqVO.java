package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class K2KBInquiryInterBankReqVO {

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("msisdn")
    private String msisdn;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("payment_type")
    private String paymentType;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("form_name")
    private String formName;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("provider")
    private String provider;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("bank_code")
    private String bankCode;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("destination_account_no")
    private String destAcc;
}
