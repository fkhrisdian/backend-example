package com.kaspro.bank.vo;

import com.kaspro.bank.persistance.domain.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RegisterPartnerVO extends BaseVO {

    @NotBlank(message = "transID is required")
    private String transID;

    @NotBlank(message = "serviceName is required")
    private String serviceName;

    @NotBlank(message = "partner is required")
    private Partner partner;

    @NotBlank(message = "dataPIC is required")
    private DataPIC dataPIC;

    @NotBlank(message = "ListLampiran is required")
    private List<Lampiran> listLampiran;

    @NotBlank(message = "transferFees is required")
    private List<TransferFee> transferFees;

    @NotBlank(message = "transferFees is required")
    private String[] listTier;

    private List<TransferLimit> transferLimitList;

//    private List<AuditTrail> auditTrails;

}
