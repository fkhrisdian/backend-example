package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WrapperTransferReqVO {

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("tid")
    private String tid;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("payment_method")
    private String paymentMethod;

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("sku")
    private String sku;

    @ApiModelProperty(value = "Optional",required = false)
    @JsonProperty("remark")
    private String remark;

}
