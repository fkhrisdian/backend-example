package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInHousePaymentRespVO {
  private PaymentResponse doPaymentResponse;

  @Data
  public static class PaymentResponse {
    private String clientId;
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String errorMessage;
      private String responseTimestamp;
      private String debitAccountNo;
      private String creditAccountNo;
      private String valueAmount;
      private String valueCurrency;
      private String customerReference;
    }
  }
}
