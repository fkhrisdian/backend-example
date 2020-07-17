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
}
