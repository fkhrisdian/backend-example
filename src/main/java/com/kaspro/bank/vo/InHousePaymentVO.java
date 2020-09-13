package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class InHousePaymentVO {
  private String debitAccountNo;
  private String creditAccountNo;
  private String amount;
  private String remark;
  private String paymentMethod;
  private String destinationBankCode;
  private String chargingModelId;
  private String beneficiaryEmailAddress;
  private String beneficiaryName;
  private String beneficiaryAddress1;
  private String beneficiaryAddress2;

}
