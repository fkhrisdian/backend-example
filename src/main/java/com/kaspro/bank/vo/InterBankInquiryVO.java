package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class InterBankInquiryVO {
  private String accountNo;
  private String destinationBankCode;
  private String destinationAccountNo;
}
