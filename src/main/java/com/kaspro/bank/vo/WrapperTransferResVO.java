package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WrapperTransferResVO {

    @ApiModelProperty(value = "Mandatory",required = true)
    @JsonProperty("refference_id")
    private String reffId;
    
}
