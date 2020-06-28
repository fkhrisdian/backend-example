package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInHousePaymentReqVO extends OgpBaseReqVO {
  private String customerReferenceNumber;
  private String paymentMethod;
  private String debitAccountNo;
  private String creditAccountNo;
  private String valueDate;
  private String valueCurrency;
  private String valueAmount;
  private String remark;
  private String beneficiaryEmailAddress;
  private String beneficiaryName;
  private String beneficiaryAddress1;
  private String beneficiaryAddress2;
  private String destinationBankCode;
  private String chargingModelId;
}
