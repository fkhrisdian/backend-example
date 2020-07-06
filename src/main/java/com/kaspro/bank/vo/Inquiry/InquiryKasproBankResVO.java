package com.kaspro.bank.vo.Inquiry;

import lombok.Data;

@Data
public class InquiryKasproBankResVO {
    private String tid;
    private String sourceAccount;
    private String sourceName;
    private String destinationAccount;
    private String destinationName;
    private String amount;
    private String adminFee;
    private String interBankFee;
    private String totalAmount;
}
