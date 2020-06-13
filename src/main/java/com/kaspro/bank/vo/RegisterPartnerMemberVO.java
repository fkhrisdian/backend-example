package com.kaspro.bank.vo;

import com.kaspro.bank.persistance.domain.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RegisterPartnerMemberVO extends BaseVO {

    @NotBlank(message = "transID is required")
    private String transID;

    @NotBlank(message = "serviceName is required")
    private String serviceName;

    @NotBlank(message = "partnerMember is required")
    private PartnerMember partnerMember;

    @NotBlank(message = "dataPIC is required")
    private DataPIC dataPIC;

    @NotBlank(message = "listLampiranMember is required")
    private List<Lampiran> listLampiran;

    @NotBlank(message = "listTransferInfoMember is required")
    private List<TransferInfoMember> listTransferInfoMember;

    private VirtualAccount virtualAccount;

    private List<AuditTrail> auditTrails;

}
