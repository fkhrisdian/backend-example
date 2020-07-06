package com.kaspro.bank.vo.TransferKasproBank;

import lombok.Data;

@Data
public class TransferKasproBankResVO {
    private String tid;
    private String sourceAccount;
    private String destinationAccount;
    private String amount;
    private String adminFee;
    private String interBankFee;
    private String totalAmount;
    private String status;
    private String remark;
}
