package com.kaspro.bank.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class TransferFeeVO {

    @NotBlank(message = "kasproBank is required")
    private BigDecimal kasproBank;

    @NotBlank(message = "kaspro is required")
    private BigDecimal kaspro;

    @NotBlank(message = "BNI is required")
    private BigDecimal bni;

    @NotBlank(message = "otherBank is required")
    private BigDecimal otherBank;

    @NotBlank(message = "eMoney is required")
    private BigDecimal emoney;

}
