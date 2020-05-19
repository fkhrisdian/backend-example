package com.kaspro.bank.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class PartnerRequestVO extends BaseVO {

    @NotBlank(message = "transID is required")
    private String transID;

    @NotBlank(message = "serviceName is required")
    private String serviceName;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "npwp is required")
    private String npwp;

    @NotBlank(message = "nibSiupTdp is required")
    private String nibSiupTdp;

    @NotBlank(message = "noAktaPendirian is required")
    private String noAktaPendirian;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "transferLimits is required")
     List<TransferLimitVO> transferLimits;

    @NotBlank(message = "transferFees is required")
    TransferFeeVO transferFee;

}
