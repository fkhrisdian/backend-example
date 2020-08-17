package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class K2KBPaymentInhouseReqVO {

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

    @ApiModelProperty(value = "Optional",required = false)
    @JsonProperty("bank_code")
    private String bankCode;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("payment_method")
    private String paymentMethod;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("destination_account_no")
    private String destAcc;

    @ApiModelProperty(value = "Optional",required = false)
    @JsonProperty("beneficiary_email")
    private String email;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("beneficiary_name")
    private String name;

    @ApiModelProperty(value = "Optional",required = false)
    @JsonProperty("beneficiary_address1")
    private String address1;

    @ApiModelProperty(value = "Optional",required = false)
    @JsonProperty("beneficiary_address2")
    private String address2;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("remark")
    private String remark;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("amount")
    private String amount;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("fee")
    private String fee;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("total")
    private String total;
}
