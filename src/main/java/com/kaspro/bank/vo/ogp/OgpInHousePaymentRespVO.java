package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInHousePaymentRespVO {
  private PaymentResponse doPaymentResponse;

  @Data
  public static class PaymentResponse {
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String debitAccountNo;
      private String creditAccountNo;
      private String valueAmount;
      private String valueCurrency;
      private String bankReference;
      private String customerReference;
    }
  }
}
