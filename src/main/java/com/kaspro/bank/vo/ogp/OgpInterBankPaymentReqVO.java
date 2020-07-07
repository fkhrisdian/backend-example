package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInterBankPaymentReqVO extends OgpBaseReqVO {
  private String customerReferenceNumber;
  private String amount;
  private String destinationAccountNum;
  private String destinationAccountName;
  private String destinationBankCode;
  private String destinationBankName;
  private String accountNum;
  private String retrievalReffNum;
}
