package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class InterBankPaymentVO {
  private String accountNo;
  private String amount;
  private String destinationAccountNo;
  private String destinationAccountName;
  private String destinationBankCode;
  private String destinationBankName;
  private String retrievalReffNo;
}
