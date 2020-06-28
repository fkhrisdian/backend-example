package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInterBankInquiryReqVO extends OgpBaseReqVO {
  private String customerReferenceNumber;
  private String accountNum;
  private String destinationBankCode;
  private String destinationAccountNum;
}
