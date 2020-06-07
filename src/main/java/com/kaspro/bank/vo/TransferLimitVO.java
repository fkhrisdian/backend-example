package com.kaspro.bank.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TransferLimitVO {

    @NotBlank(message = "type is required")
    private String type;

    List<KeyValuePairedVO> attributes;

}
